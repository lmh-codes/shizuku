# Shizuku Android 17 补丁说明

> 仓库：https://github.com/lmh-codes/shizuku  
> 正式版：`13.7.1`（首页服务版本 `13.7`）  
> 唯一上游：官方 [RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku) / [RikkaApps/Shizuku-API](https://github.com/RikkaApps/Shizuku-API)

## 包含

- Android 17 Compat / mDNS host+port / 本地网络权限 / SDK 37
- 首页授权数 onResume 刷新；AppsViewModel generation
- 启动时不下发 sendBinderToClient；BinderSender 仅前台推送
- **免 Root 开机自启**：`AdbAutoStartJobService` + `BootCompleteReceiver`（Android 13+ / WRITE_SECURE_SETTINGS / 上次为 ADB 启动）
- 关于页源码链接：https://github.com/lmh-codes/shizuku
