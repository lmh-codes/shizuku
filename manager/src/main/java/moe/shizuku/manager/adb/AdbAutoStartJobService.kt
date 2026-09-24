package moe.shizuku.manager.adb

import android.Manifest
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import moe.shizuku.manager.ShizukuSettings
import moe.shizuku.manager.starter.Starter
import rikka.shizuku.Shizuku
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 无 Root 开机自动启动：通过 JobScheduler 打开无线调试、发现端口并执行 Starter。
 * 需 Android 13+，且已授予 WRITE_SECURE_SETTINGS，上次启动方式为 ADB。
 */
@RequiresApi(Build.VERSION_CODES.R)
class AdbAutoStartJobService : JobService() {

    private var runningJob: kotlinx.coroutines.Job? = null

    override fun onStartJob(params: JobParameters): Boolean {
        runningJob?.cancel()
        runningJob = CoroutineScope(Dispatchers.IO).launch {
            val started = try {
                startShizuku()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                false
            }
            jobFinished(params, !started)
        }
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        runningJob?.cancel()
        runningJob = null
        return true
    }

    private suspend fun startShizuku(): Boolean {
        if (Shizuku.pingBinder()) return true
        if (ShizukuSettings.getLastLaunchMode() != ShizukuSettings.LaunchMethod.ADB) return false
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            || checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED
        ) return false

        Settings.Global.putInt(contentResolver, "adb_wifi_enabled", 1)
        Settings.Global.putInt(contentResolver, Settings.Global.ADB_ENABLED, 1)
        Settings.Global.putLong(contentResolver, "adb_allowed_connection_time", 0L)

        repeat(MAX_ATTEMPTS) {
            if (Shizuku.pingBinder()) return true
            if (tryConnect()) return true
            delay(RETRY_DELAY_MS)
        }
        return false
    }

    private suspend fun tryConnect(): Boolean {
        val connected = AtomicBoolean(false)
        val attemptedPorts = ConcurrentHashMap.newKeySet<Int>()
        val latch = CountDownLatch(1)
        val executor = Executors.newSingleThreadExecutor()
        val mdns = AdbMdns(this, AdbMdns.TLS_CONNECT) { (host, port) ->
            if (port <= 0 || connected.get() || !attemptedPorts.add(port)) return@AdbMdns
            try {
                executor.execute {
                    try {
                        val key = AdbKey(PreferenceAdbKeyStore(ShizukuSettings.getPreferences()), "shizuku")
                        AdbClient(host, port, key).use {
                            it.connect()
                            it.shellCommand(Starter.internalCommand, null)
                        }
                        connected.set(true)
                        latch.countDown()
                    } catch (_: Exception) {
                    }
                }
            } catch (_: RuntimeException) {
            }
        }
        return try {
            mdns.start()
            runInterruptible { latch.await(DISCOVERY_TIMEOUT_MS, TimeUnit.MILLISECONDS) }
            connected.get()
        } finally {
            mdns.stop()
            executor.shutdownNow()
        }
    }

    override fun onDestroy() {
        runningJob?.cancel()
        super.onDestroy()
    }

    companion object {
        private const val JOB_ID = 13701
        private const val MAX_ATTEMPTS = 6
        private const val RETRY_DELAY_MS = 2_000L
        private const val DISCOVERY_TIMEOUT_MS = 5_000L

        fun schedule(context: Context) {
            val scheduler = context.getSystemService(JobScheduler::class.java) ?: return
            val component = ComponentName(context, AdbAutoStartJobService::class.java)
            val info = JobInfo.Builder(JOB_ID, component)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setBackoffCriteria(10_000L, JobInfo.BACKOFF_POLICY_EXPONENTIAL)
                .build()
            scheduler.schedule(info)
        }
    }
}
