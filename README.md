# Hello Day

A mindful daily ritual companion and routine tracker for Android, built with Jetpack Compose and modern Material 3 design tokens.

## ✨ Highlights

- 🌅 **Intentional Morning & Evening Rituals:** Guided ritual timelines with customizable cues (Sunrise, Breathe, Hydrate, Commute, Focus, Exercise, Wind Down).
- ⏰ **Reliable Precision Wake-Up Engine:** Background exact alarm scheduling (`RTC_WAKEUP` / `setExactAndAllowWhileIdle`), lock-screen alarm activity, and system reboot recovery.
- 📅 **Calm Day Planner:** Grouped routines, tasks with due dates, habits with streaks, daily events, and notes from "future-you".
- 🎨 **Flux Design Tokens:** Harmonic 4dp/8dp grid, semantic surface tiers, high-contrast typography, and accessible motion controls with full Reduce Motion support.
- 📱 **Home Screen Widget:** Glanceable upcoming ritual alarm countdown and quick triggers right on your home screen.

## 🛠️ Tech Stack & Architecture

- **UI:** Jetpack Compose, Material 3, Custom Design System (`FluxTheme`)
- **Architecture:** Clean Architecture with MVVM, Hilt dependency injection, Kotlin Coroutines & Flow
- **Persistence:** Room Database (Reactive DAOs), Preferences DataStore
- **Background & Alarms:** Android AlarmManager, Foreground Service (`mediaPlayback`), BroadcastReceivers
- **Testing:** JUnit 4 semantic tests (`RitualSemanticTest`)

## 🚀 Building & Running

1. Open in Android Studio (or run from the command line).
2. Set your Android SDK path in `local.properties`:
   ```properties
   sdk.dir=/path/to/Android/Sdk
   ```
3. Run unit tests:
   ```bash
   ./gradlew testDebugUnitTest
   ```
4. Assemble Debug APK:
   ```bash
   ./gradlew assembleDebug
   ```

## 📄 License
This project is licensed under the terms of the MIT / Apache 2.0 license.
