Copilot Instructions — helpdesk_chat_app

Purpose
- Define project layout, common commands, environment, and coding style for Copilot and contributors.

Project structure
- /app            — Android application module
- build.gradle.kts, settings.gradle.kts — Gradle configuration

Build & run (common commands)
- ./gradlew :app:installDebug --no-daemon  # build & install to connected device/emulator
- adb devices -l                             # list devices
- adb -s <serial> shell am start -n haag.your.next.developer/.MainActivity  # launch
- ./gradlew assembleRelease                  # build release

Environment
- JDK 17+ (tested with Temurin 17/26), Android SDK with compileSdk 36
- ANDROID_HOME / ANDROID_SDK_ROOT set for CI
- Gradle wrapper is checked in — use ./gradlew

Device guidance
- Enable Developer options → USB debugging on physical devices
- Use a good USB cable and set USB mode to File transfer

Coding & style
- Kotlin + Jetpack Compose idioms
- Prefer immutability, single-activity + navigation3
- Keep Compose functions small and testable
- Follow AndroidX and Kotlin coding conventions

Testing
- Unit tests: ./gradlew test
- Instrumented tests: ./gradlew connectedAndroidTest (emulator/device required)

CI/Automation
- Use Gradle wrapper and set JAVA_HOME in CI
- Cache Gradle and Android SDK between runs

Copilot preferences / guidance
- Project root is the primary context
- Prioritize tasks: build/run, fix failing tests, follow package name haag.your.next.developer
- When making changes, update this file with important workflow edits
- Each time a function has been created and updated you need to update the unit tests for it, all edge cases

Useful scripts (suggested)
- ./run-emulator.sh  — shortcut to build/install and launch on emulator

Contacts & notes
- Maintainer: add name/email here
- Add any device-specific notes (Pixel 10 guidance) below

(End of file)
