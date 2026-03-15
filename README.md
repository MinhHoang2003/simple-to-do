# SimpleToDo

A clean, minimal Android to-do app built with Jetpack Compose and MVVM + Clean Architecture.

## Features

- **Today / Week view** — switch between a single-day focus and a full week strip with date navigation
- **Add & edit tasks** — set title, description, priority (Low / Medium / High), due date, and time
- **Swipe to delete** with undo via Snackbar
- **Quick add** — type a task directly in the list without opening a dialog
- **Statistics screen** — monthly overview with:
  - Total / Completed / Completion rate summary cards
  - Priority breakdown (High / Medium / Low) with progress bars
  - Daily bar chart showing tasks per day across the full month
- **Material You** — dynamic color on Android 12+, dark mode support

## Tech Stack

| Layer | Library |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material3 (BOM 2024.12.01) |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt 2.51.1 |
| Database | Room 2.6.1 (KSP) |
| Async | Kotlin Coroutines + StateFlow |
| Navigation | Navigation Compose 2.8.5 |
| Lifecycle | Lifecycle 2.8.7 |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 36 (Android 15) |

## Architecture

The project follows Clean Architecture with a strict one-way dependency rule:

```
ui  →  domain  ←  data
```

```
app/
└── com.product.hstudio.simpletodo/
    ├── data/
    │   ├── local/
    │   │   ├── dao/          # Room DAOs
    │   │   ├── database/     # TodoDatabase
    │   │   └── entity/       # Room entities + mappers
    │   └── repository/       # TodoRepositoryImpl
    ├── domain/
    │   ├── model/            # Todo, Priority (pure Kotlin)
    │   ├── repository/       # TodoRepository interface
    │   └── usecase/          # One class per use case
    ├── ui/
    │   ├── todolist/         # TodoListScreen + TodoViewModel
    │   │   └── component/    # TodoItem, AddEditTodoDialog
    │   ├── statistics/       # StatisticsScreen + StatisticsViewModel
    │   ├── theme/            # SimpleTodoTheme (Material3)
    │   └── MainActivity.kt
    └── di/
        └── AppModule.kt      # Hilt providers
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 26+
- JDK 11

### Build & Run

```bash
# Clone the repository
git clone https://github.com/MinhHoang2003/simple-to-do.git
cd simple-to-do

# Build debug APK
./gradlew build

# Install on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Clean build artifacts
./gradlew clean
```
