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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import moe.shizuku.manager.ShizukuSettings
import moe.shizuku.manager.starter.Starter
import rikka.shizuku.Shizuku
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

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
        val latch = CountDownLatch(1)
        val mdns = AdbMdns(this, AdbMdns.TLS_CONNECT) { port ->
            if (port <= 0 || connected.get()) return@AdbMdns
            try {
                val key = AdbKey(PreferenceAdbKeyStore(ShizukuSettings.getPreferences()), "shizuku")
                AdbClient("127.0.0.1", port, key).use { it.connect(); it.shellCommand(Starter.internalCommand, null) }
                connected.set(true)
            } catch (_: Exception) {
            } finally {
                latch.countDown()
            }
        }
        return try {
            mdns.start()
            runInterruptible { latch.await(DISCOVERY_TIMEOUT_MS, TimeUnit.MILLISECONDS) }
            connected.get()
        } finally {
            mdns.stop()
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
