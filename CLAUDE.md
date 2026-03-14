# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Architecture

MVVM + Clean Architecture. Every business logic must go through a Use Case.

### Packages

**`data`** — data sources only (Room DB, future network). No business logic.
- `data/local/entity/` — Room entities + `toDomain()` / `toEntity()` mapper extensions
- `data/local/dao/` — Room DAOs (operate on entities only)
- `data/local/database/` — `TodoDatabase` singleton
- `data/repository/` — `TodoRepositoryImpl` implementing the domain interface

**`domain`** — pure Kotlin, zero Android/Room imports.
- `domain/model/` — `Todo`, `Priority` (plain data classes/enums)
- `domain/repository/` — `TodoRepository` interface
- `domain/usecase/` — one class per use case (`GetTodosUseCase`, `AddTodoUseCase`, `UpdateTodoUseCase`, `DeleteTodoUseCase`, `ToggleTodoUseCase`)

**`ui`** — Jetpack Compose only, no XML layouts.
- `ui/screen/` — full screens (`TodoListScreen`)
- `ui/screen/component/` — reusable composables (`TodoItem`, `AddEditTodoDialog`)
- `ui/theme/` — `SimpleTodoTheme` (Material3, dynamic color on Android 12+)
- `ui/TodoViewModel.kt` — `@HiltViewModel`, exposes `StateFlow<TodoListUiState>`

**`di`** — Hilt dependency injection.
- `di/AppModule.kt` — `@Singleton` providers: `TodoDatabase`, `TodoDao`, `TodoRepository`

### Key design decisions

- `Todo` domain model is pure Kotlin — no `@Parcelize`, no Room annotations
- `priority` stored as `String` (enum name) in `TodoEntity` — no `TypeConverter` needed
- Sorting (by completion → priority → createdAt) done in `GetTodosUseCase`, not SQL
- Dependency direction: `ui` → `domain` ← `data`. `domain` never imports `data` or `ui`
- `TodoListUiState.deletedTodo` drives the Snackbar undo flow in `TodoListScreen`

### Dependency injection (Hilt)

- `SimpleTodoApplication` is annotated `@HiltAndroidApp`
- `MainActivity` is annotated `@AndroidEntryPoint`
- `TodoViewModel` uses `@HiltViewModel` + `@Inject constructor`
- All use cases use `@Inject constructor`
- `TodoListScreen` obtains its ViewModel via `hiltViewModel()`
- `AppModule` uses `@Provides` (not `@Binds`) for all bindings — keep this consistent

## Tech Stack

| Layer | Library |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material3 (BOM 2024.12.01) |
| DI | Hilt 2.51.1 |
| Database | Room 2.6.1 (KSP) |
| Async | Kotlin Coroutines + StateFlow |
| Lifecycle | Lifecycle 2.8.7 (viewmodel-compose, runtime-compose) |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 36 (Android 15) |

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

## Key Notes

- All dependency versions are managed in `gradle/libs.versions.toml` — add new deps there first
- `TodoDatabase` uses `fallbackToDestructiveMigration()` during development; add proper migrations before release
- ProGuard/R8 minification is disabled; enable `isMinifyEnabled` in `app/build.gradle.kts` before release
- Dynamic color (Material You) is enabled on Android 12+ via `dynamicDarkColorScheme` / `dynamicLightColorScheme`
