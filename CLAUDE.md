# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## App architect design

Using MVVM with clean, every business logic must using Use Case

1. data package - contain logic for data model handling (Using Room database)
2. domain package - contain logic for business (UseCase + Repository patter)
3. ui package - contain the UI logic (using Compose UI)

## Build Commands

```bash
# Build debug APK
./gradlew build

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.product.hstudio.simpletodo.ExampleUnitTest"

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Clean build artifacts
./gradlew clean
```

## Architecture

Single-module Android app (`com.product.hstudio.simpletodo`) using:
- **Language:** Kotlin 2.0.21
- **UI:** Material Design Components (DayNight theme with light/dark support)
- **Min SDK:** 26 (Android 8.0), **Target SDK:** 36 (Android 15)
- **Java/Kotlin JVM target:** 11

## Project Structure

- `app/src/main/` — Application source; Java package is currently empty (starter template)
- `app/src/test/` — JUnit 4 unit tests
- `app/src/androidTest/` — Espresso instrumented tests
- `gradle/libs.versions.toml` — Centralized version catalog for all dependencies
- `app/build.gradle.kts` — App module config (SDK versions, dependencies)

## Key Notes

- Dependency versions are managed centrally in `gradle/libs.versions.toml` — add new deps there first
- ProGuard/R8 minification is disabled for release builds; enable `isMinifyEnabled` in `app/build.gradle.kts` when needed
- No activities are defined yet in `AndroidManifest.xml` — this is a fresh starter template
