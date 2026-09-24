# Shizuku Android 17 适配版

基于 [RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku) 的 Android 17 适配版本。

Shizuku 可以让支持它的应用通过 ADB 或 root 权限调用部分系统 API，避免每次操作都创建 `su` 进程或解析命令行输出。

## 当前版本

- 版本：`13.7.1`（正式版）
- 首页服务版本：`13.7`
- 适配目标：Android 17 / API 37
- applicationId：`moe.shizuku.privileged.api`
- 源码：https://github.com/lmh-codes/shizuku

## 更新内容（13.7.1）

- **新增免 Root 开机自动启动**：在 Android 13+ 且曾用无线调试成功启动过一次的前提下，开机后通过 JobScheduler 自动开启无线调试并拉起 Shizuku
- 无线调试 mDNS 使用解析到的 host+port，提升配对/启动连接成功率
- 保留 13.7.0：授权数量实时刷新、限制后台主动弹授权、Android 17 兼容等

## APK 下载

前往 [Releases](https://github.com/lmh-codes/shizuku/releases/latest) 下载最新正式版 APK。

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
6. **免 Root 开机自启**：用无线调试成功启动一次后（系统会授予必要权限），重启设备即可自动尝试拉起；需 Android 13+。

## 说明

- 本仓库为 Android 17 兼容项目，不代表 Shizuku 官方发布版本。
- Release keystore 仅保存在构建机的安全目录中，没有上传到仓库；后续更新必须继续使用同一签名，否则 Android 无法覆盖安装。
- 上游项目及官方文档：[RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku)
- 官方用户指南：[shizuku.rikka.app](https://shizuku.rikka.app/)

## 许可证

项目代码沿用上游 Apache License 2.0，详情见 [LICENSE](LICENSE)。

请同时遵守上游关于名称、applicationId、权限声明和图标资源的附加限制；不得将上游图标用于展示 Shizuku 以外的用途。
