@echo off
setlocal
cd /d "%~dp0"
call gradlew.bat :app:assembleDebug :app:testDebugUnitTest :service-android:testDebugUnitTest :core-protocol:test --console=plain
if errorlevel 1 (
  echo Build failed. Review the error above.
  pause
  exit /b 1
)
echo APK: %CD%\app\build\outputs\apk\debug\app-debug.apk
pause
