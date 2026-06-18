# Coding Principles

These principles govern how all implementation code is written in this project. They apply to every Composable, ViewModel, use case, repository, and utility. Following them is not optional — they exist to keep the codebase maintainable, readable, and consistent as the project grows.

---

## Core engineering principles

### KISS — Keep It Simple, Stupid
Write the simplest code that correctly solves the problem. Avoid clever abstractions, premature generalisation, and over-engineering. A future reader should understand the code in one pass.

### DRY — Don't Repeat Yourself
Any logic duplicated in two places must be extracted. The extraction point depends on scope:
- Used by one screen → colocate in that screen's ViewModel or a private helper.
- Used by two or more screens → promote to `domain/usecase/`, `ui/common/components/`, or `util/`.

### YAGNI — You Aren't Gonna Need It
Do not add parameters, abstractions, or config for features not yet required. Implement what the task asks for, nothing more.

### Single Responsibility
Every function, use case, and class does one thing. If you find yourself writing "and" in a description, split it. One `UseCase` per operation.

### Composition over inheritance
Build complex UI by composing small, focused Composables. Prefer delegation and interfaces over class inheritance for non-ViewModel code.

---

## Separation of concerns — the hard rule

| What you are writing | Where it goes |
|---|---|
| Renders UI / Composables | `ui/<feature>/`, `ui/common/components/` |
| Manages state and events | `domain/viewmodel/` |
| Business logic / validation | `domain/usecase/` |
| Data access / Firebase calls | `data/repository/` |
| Pure Kotlin helpers | `util/` |

A screen file (`<Feature>Screen.kt`) is a thin rendering layer: it receives state via parameters and invokes lambdas on user interaction. It does not contain business logic, `viewModelScope`, or Firebase imports.

---

## ViewModel rules

- `@HiltViewModel`, constructor-inject use cases only — no repository injection directly into ViewModels.
- Extend `BaseViewModel<T>`.
- Expose state as `StateFlow<UiState<T>>` (immutable, via `.asStateFlow()`).
- One-shot events (navigation, toasts) via `MutableSharedFlow(extraBufferCapacity = 1, replay = 0)`.
- All work runs in `viewModelScope.launch`.
- `fold`/`onSuccess`/`onFailure` the `Result` into `UiState` — never call `.getOrThrow()`.
- Keep `init {}` blocks minimal — stub them in tests before constructing the ViewModel.

```kotlin
// good — one-shot navigation
private val _navigateToChat = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
val navigateToChat = _navigateToChat.asSharedFlow()

// bad — navigation as UiState boolean (re-fires on recomposition)
data class LoginUiState(val shouldNavigate: Boolean = false)
```

---

## UseCase rules

- Extend the correct base class (`UseCase<P,R>`, `ProducerUseCase<R>`, or `ConsumerUseCase<P>`).
- Constructor-inject the repository interface with `@Inject constructor`.
- Thin by design: validate input, call the repository, return the `Result`. No Firebase SDK imports.
- One file per use case. Name: `<Action><Subject>UseCase.kt`.

---

## Repository rules

- Define an interface in `data/interfaces/`. Implement it in `data/repository/`.
- All public functions are `suspend` and return `Result<T>`.
- Wrap every Firebase call: `try { Result.success(…) } catch (e: Exception) { Result.failure(e) }`.
- Never throw exceptions to callers. Never return raw Firebase types to the domain layer.
- Map Firestore DTOs to domain models inside the repository using mappers from `data/mapper/`.

---

## Compose rules

- **Split Route / Screen**: `<Feature>Route` is stateful (collects ViewModel state), `<Feature>Screen` is stateless (parameters only, has a `@Preview`).
- Use `collectAsStateWithLifecycle()`, not `collectAsState()`.
- Collect one-shot events in `LaunchedEffect(Unit)` with `sharedFlow.collect { … }`.
- Use `StateHandler` for `UiState` — never duplicate the Loading / Error / Success switch per screen.
- No side effects in the `@Composable` body outside of effect APIs (`LaunchedEffect`, `SideEffect`, `DisposableEffect`).
- No business logic, no suspend calls, no repository references inside `@Composable` functions.
- Keep Composables small and focused — if a composable exceeds ~60 lines, consider extracting sub-components.

---

## Kotlin rules

- No `!!` — use safe calls (`.?`), `let`, `Elvis (?:)`, or explicit null checks.
- No `var` in data/domain models — use `val` and immutable data classes.
- Prefer `val` over `var` everywhere. Use `var` only when mutation is genuinely required.
- No `any` — use explicit types, generics, or sealed classes.
- Named arguments for functions with more than two parameters of the same type.
- Use data classes for all consumer/producer models. No mutable POJOs.
- Extension functions in `data/mapper/` or `domain/mapper/` — not scattered across feature files.

---

## Error handling rules

- Repositories return `Result<T>` — the only layer that touches Firebase exceptions.
- Use cases pass `Result<T>` through unchanged.
- ViewModels `fold` into `UiState.Success` / `UiState.Error`, or emit a toast for non-fatal errors.
- Never swallow exceptions silently. Never log and continue without surfacing an error state.

---

## Code style

- No comments that explain *what* the code does — well-named identifiers do that.
- Add a comment only when the *why* is non-obvious: a hidden constraint, a workaround, an invariant.
- Delete dead code. Do not comment it out.
- No hardcoded user-visible strings — use `strings.xml`.
- Dependency versions declared in `gradle/libs.versions.toml`, referenced via `libs.*` in `build.gradle.kts`. Never hardcode a version string in a build file.

---

## Reuse checklist — run before creating anything new

Before writing a new Composable, use case, or utility:

1. Does it already exist in `ui/common/components/`, `domain/usecase/`, or `util/`?
2. Is there a similar one that can be made slightly more generic without violating YAGNI?

If yes to either — extend or reuse. Do not duplicate.

---

## Pre-completion checklist

Before marking any implementation task done, verify every item:

- [ ] Architecture principles (`docs/ai/architecture-principles.md`) fully followed — package placement, naming, layer separation.
- [ ] New repository interface bound in `di/RepositoryModule.kt`.
- [ ] UseCase extends the correct base class for its data direction.
- [ ] ViewModel uses `StateFlow` for state and `SharedFlow` for one-shot events — no navigation booleans in `UiState`.
- [ ] All repository functions return `Result<T>` — no thrown exceptions crossing layer boundaries.
- [ ] No `!!` used anywhere.
- [ ] No Firebase SDK imports outside `data/repository/` and `di/`.
- [ ] Screen split into `<Feature>Route` (stateful) and `<Feature>Screen` (stateless + `@Preview`).
- [ ] `collectAsStateWithLifecycle()` used in all Composables collecting a flow.
- [ ] No business logic in Composables.
- [ ] Consumer models in `domain/model/consumer/`, producer models in `domain/model/producer/`. No `Pair`/`Triple` for structured data.
- [ ] No hardcoded user-visible strings — use `strings.xml`.
- [ ] Dependency versions in `libs.versions.toml`, not hardcoded in `build.gradle.kts`.
- [ ] No dead code, no commented-out blocks.
- [ ] File and class names match the architecture naming conventions table.
