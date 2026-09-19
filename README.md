# Shizuku Android 17 适配版

这是基于 [RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku) 制作的 Android 17 适配测试版本。

Shizuku 可以让支持它的应用通过 ADB 或 root 权限调用部分系统 API，避免每次操作都创建 `su` 进程或解析命令行输出。

## 当前版本

- 版本：`13.7.0.r5.9d1351f`
- 适配目标：Android 17 / API 37
- applicationId：`moe.shizuku.privileged.api`
- 发布类型：测试预发布版（Release 签名）

## 更新内容

- 恢复首页中间的“已授权应用”列表入口。
- 修复尚未完成无线调试配对时，点击“启动”没有进入配对页面的问题。
- 完成配对后，点击“启动”会进入无线 ADB 连接流程。
- 增加无线 ADB 配对状态记录。
- 补充 Android 17 兼容接口及相关权限适配。
- 修复部分 Android 17 系统 API 调用兼容问题。
- 修复从应用管理页返回首页后，已授权应用数量不及时刷新的问题。
- 修复并发刷新时旧结果覆盖最新授权数量的问题。
- 调整 Binder 分发时机：后台应用不会仅因进程或 UID 状态变化提前收到 Binder。
- 只有目标应用进入前台并主动调用 Shizuku 时，才会显示权限确认。
- 版本号更新为 13.7.0。

## APK 下载

前往 [v13.7.0 发布页面](https://github.com/lmh-codes/shizuku/releases/tag/v13.7.0) 下载：

```text
shizuku-v13.7.0.r5.9d1351f-release.apk
```

SHA-256：

```text
3DBBBAFBFFBA65EE9A0FA1833C4718249868124F8C629E5743FE263C29072387
```

> 当前 APK 已改用独立 Release 证书签名，不再使用 Android Debug 证书。安全软件仍可能因为 Shizuku 的 ADB、系统服务和高权限能力进行行为检测，因此不能保证所有设备都不报毒。

## 构建

Windows PowerShell：

```powershell
$env:JAVA_HOME = 'E:\Java\jdk-21.0.8+9'
$env:ANDROID_HOME = 'E:\Android\Sdk'
$env:ANDROID_SDK_ROOT = 'E:\Android\Sdk'
.\gradlew.bat :manager:assembleRelease --no-daemon
```

项目需要 JDK 21、Android SDK 37 和对应 NDK。源码仓库已经包含本项目使用的 Shizuku API 模块，不需要另外拉取子模块。

## 使用说明

1. Android 11 及以上可以通过“无线调试”完成配对并启动 Shizuku。
2. 首次使用时先开启开发者选项和无线调试。
3. 尚未配对时点击“启动”，应用会自动进入配对流程。
4. 配对完成后再次点击“启动”，进入无线 ADB 连接流程。
5. Shizuku 启动成功后，可在首页查看和管理已授权应用。

## 说明

- 本仓库为 Android 17 兼容测试项目，不代表 Shizuku 官方发布版本。
- 尚未完成 Android 17 真机全流程验证。
- Release keystore 仅保存在构建机的安全目录中，没有上传到仓库；后续更新必须继续使用同一签名，否则 Android 无法覆盖安装。
- 上游项目及官方文档：[RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku)
- 官方用户指南：[shizuku.rikka.app](https://shizuku.rikka.app/)

## 许可证

项目代码沿用上游 Apache License 2.0，详情见 [LICENSE](LICENSE)。

请同时遵守上游关于名称、applicationId、权限声明和图标资源的附加限制；不得将上游图标用于展示 Shizuku 以外的用途。
