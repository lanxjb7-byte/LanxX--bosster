# LanxX Booster (Java)

Native Android Java starter project. Minimum Android 10 (API 29), compile SDK 35, target SDK 35.

## Included
- Animated LanxX intro and black/red dashboard
- Shizuku binder + permission check/request (API 13.1.5)
- Floating L overlay (requires Android "Display over other apps" permission)
- Free Fire / Free Fire MAX launch intents
- Battery, available RAM and display refresh-rate readouts
- Safe optimization guidance and Android storage settings shortcut
- GitHub Actions workflow to build a debug APK

## Important limitations
- Shizuku permission is a user consent gate here; this starter does not run privileged commands or modify other apps.
- Android does not provide ordinary apps with a universal real-time per-game FPS/GPU monitor. "DISPLAY HZ" is the display refresh rate, not the game's FPS.
- The floating panel is a quick utility panel, not an in-game cheat/aim/click automation tool.
- Game launch works only if the corresponding official game package is installed. Package IDs used: `com.dts.freefireth` and `com.dts.freefiremax`.

## Build on GitHub
1. Create a new GitHub repository.
2. Upload the contents of this ZIP to the repository root (not the ZIP file itself).
3. Open **Actions** and enable workflows if GitHub asks.
4. Select **Build LanxX Booster APK** and press **Run workflow**, or push to `main`/`master`.
5. When the run finishes, open the run and download artifact **LanxX-Booster-debug**.
6. Extract the artifact ZIP to get `app-debug.apk`.

## Shizuku setup
1. Install and start Shizuku.
2. On Android 11+, start its wireless debugging service following Shizuku's own on-screen instructions.
3. Open LanxX Booster and press **AKTIFKAN LANXX BOOSTER**.
4. Approve the Shizuku permission prompt.
5. Approve the separate Android overlay permission for the floating L bubble.

Shizuku does not automatically enable itself from another app. Its service must already be running and the user must approve the request.

## Version
1.0 • Java • applicationId `com.lanxx.booster`
