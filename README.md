# Shizuku

An Android app that allows other apps to use system-level APIs that require ADB/root privileges.

**项目地址:** https://github.com/lmh-codes/shizuku

## Requirements

**Minimum Version: Android 7+**
- **Root mode:** Requires a rooted device
- **Wireless Debugging mode:** Works on Android 11+ and all Android TVs
- **PC mode:** Works on all devices
- **Start on boot:** Available only when using Wireless Debugging or Root mode

## Building

```powershell
.\gradlew.bat :manager:assembleRelease --no-daemon
```

Requires JDK 21 and Android SDK.

## License

Licensed under [Apache 2.0](LICENSE).
