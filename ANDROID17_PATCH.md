# Official Shizuku Android 17 Patch

## Baseline

- Upstream: `https://github.com/RikkaApps/Shizuku.git`
- Baseline commit: `b844bc491f1790c72328e1a8e5b2349f8978f0ea`
- Baseline branch: `master`
- API submodule source: `https://github.com/RikkaApps/Shizuku-API.git`

This directory keeps the official Shizuku modules and package identifiers. It does not include the ShizukuX/Plus modules, alternate package names, or ShizukuX API.

## Changes

1. Add `Android17Compat` for hidden package and permission APIs whose Android 17 signatures add `deviceId`.
2. Add `InstalledPackagesCompat` for Android 17 package-list return/signature changes.
3. Route official server call sites through those compatibility helpers.
4. Add Android 16/17 local-network permissions and request the runtime permission before wireless ADB pairing.
5. Raise the official build target from SDK 36 to SDK 37 so the Android 17 permission declarations are compiled against the corresponding platform.

## Verification status

- `git diff --check`: run after the patch; source diff is limited to the files above.
- Android build: not completed in this environment. The available Gradle cache lacks `kotlin-gradle-plugin:2.1.21` for offline mode, and the online dependency resolution timed out.
- Android 17 device test: not run; requires a real/emulated API 37 device with wireless debugging and ADB access.
