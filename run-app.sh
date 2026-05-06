#!/usr/bin/env bash
set -e

# Shortcut to build & install debug APK and launch MainActivity
CMD="./gradlew :app:installDebug --no-daemon && adb shell am start -n com.example.helpdeskchatapp/.MainActivity"

echo "Running: $CMD"
# Build & install
./gradlew :app:installDebug --no-daemon
# Launch on default device (or set ADB_SERIAL to target a specific device)
if [ -n "$ADB_SERIAL" ]; then
  adb -s "$ADB_SERIAL" shell am start -n com.example.helpdeskchatapp/.MainActivity
else
  adb shell am start -n com.example.helpdeskchatapp/.MainActivity
fi
