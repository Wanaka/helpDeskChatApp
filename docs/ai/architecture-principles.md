# Architecture Principles

These principles define the structural law of the codebase. Every agent and every developer must follow them without exception. When any rule conflicts with a personal convention or training-data habit, this document wins.

---

## Package structure

```
com.example.helpdeskchatapp
├── data/
│   ├── interfaces/        # Repository interfaces — domain depends on these, never on impls
│   ├── model/             # Firestore response DTOs (data-layer only)
│   ├── mapper/            # DTO → domain entity mappers (extension functions)
│   └── repository/        # Firebase/Firestore implementations of the interfaces
├── domain/
│   ├── model/
│   │   ├── consumer/      # Use-case INPUT models (params) — e.g. Login, CreateChat, Message
│   │   └── producer/      # Use-case OUTPUT models (view entities) — e.g. ChatViewEntity
│   ├── mapper/            # Producer entity → UI ListRowEntity mappers
│   ├── usecase/           # One responsibility each; delegate to repository interfaces
│   └── viewmodel/         # @HiltViewModel; extend BaseViewModel
├── ui/
│   ├── <feature>/         # One folder per screen: <Feature>Screen.kt
│   ├── common/            # Shared composables, UiState, StateHandler
│   │   └── components/    # Reusable Compose components (used by 2+ screens)
│   └── model/             # UI-layer state models (AdminState, ChatState, ListRowEntity)
├── di/
│   ├── FirebaseModule.kt  # @Provides Firebase singletons
│   └── RepositoryModule.kt# @Binds interfaces to implementations
├── navigation/
│   ├── Navigation.kt      # NavHost + composable destinations
│   └── NavigationKeys.kt  # Sealed class / object of route keys
├── service/               # Android services (e.g. MyFirebaseMessagingService)
├── theme/                 # Color.kt, Type.kt, Theme.kt
└── util/                  # Pure Kotlin helpers with no Android/Firebase dependency
```

---

## Four-layer separation of concerns

Every piece of code belongs to exactly one layer. No exceptions.

| Layer | Location | Responsibility |
|---|---|---|
| **UI** | `ui/<feature>/`, `ui/common/components/` | Render only. Receives state from ViewModel. No business logic, no repository calls. |
| **State / behaviour** | `domain/viewmodel/` | Manage `UiState`, expose `StateFlow`, emit one-shot events. No Firebase imports. |
| **Business logic** | `domain/usecase/` | Single operation per use case. Validate and delegate to repository interface. |
| **Data access** | `data/repository/`, `data/interfaces/` | Firebase/Firestore calls wrapped in `Result<T>`. Never throw across layer boundaries. |

Dependency direction is strictly inward: `ui` → `domain` → `data/interfaces`. `data/repository` implements `data/interfaces`. No layer imports from the layer above it.

---

## UseCase type system (`domain/usecase/BaseUseCase.kt`)

Pick the base class by data direction:

| Type | Input | Output | `operator fun invoke` signature |
|---|---|---|---|
| `UseCase<P, R>` | yes | yes | `suspend operator fun invoke(params: P): R` |
| `ProducerUseCase<R>` | no | yes | `suspend operator fun invoke(): R` |
| `ConsumerUseCase<P>` | yes | Unit | `suspend operator fun invoke(params: P)` |
| `ActionUseCase` | no | Unit | `suspend operator fun invoke()` |
| `FlowUseCase<P, R>` | yes | `Flow<R>` | `operator fun invoke(params: P): Flow<R>` |

**`ActionUseCase` vs `ProducerUseCase<Result<Unit>>`**: use `ActionUseCase` only for true fire-and-forget operations that genuinely cannot fail (e.g. clearing an in-memory cache). If the operation wraps a Firebase/network call that can fail and the caller needs to react (show a toast, redirect), use `ProducerUseCase<Result<Unit>>` so the failure can propagate. `LogoutUseCase` and `PostAuthSetupUseCase` are correct examples of this.

Use cases are thin — validate input and delegate to a repository interface. No Firebase SDK imports inside use cases.

---

## ViewModel conventions

- `@HiltViewModel`, constructor-inject use cases only.
- Extend `BaseViewModel` — gives `_uiState: MutableStateFlow<UiState>` and `_toastEvent: MutableSharedFlow<String>`.
- `UiState` is a non-generic sealed class: `Loading`, `Success`, `Error(message: String)`. Screen-specific data is exposed as **separate `StateFlow` fields** on the ViewModel (e.g. `val messages`, `val chatTitle`), never carried inside `UiState`.
- Expose state as immutable `StateFlow` (`.asStateFlow()`).
- **Navigation is a one-shot event, never persistent state.** Use `MutableSharedFlow<Unit>(extraBufferCapacity = 1, replay = 0)`. Do not model navigation as a boolean in `UiState` — the ViewModel survives recomposition and will re-fire it.
- Run all work in `viewModelScope.launch`.

---

## UI conventions (Compose screens)

- Split every screen into:
  - `<Feature>Route` — stateful: calls `hiltViewModel()`, collects `StateFlow` with `collectAsStateWithLifecycle()`, collects one-shot events in `LaunchedEffect`, owns navigation lambdas.
  - `<Feature>Screen` — stateless: receives plain values and lambdas, no ViewModel reference. Add a `@Preview`.
- Render via `StateHandler` for `UiState` branches. Never duplicate the `Loading` / `Error` / `Success` switch in each screen.
- No business logic, no repository calls, no Firebase imports inside Composables.

---

## Repository conventions

- Interface defined in `data/interfaces/`. Name: `<Domain>Repository` (e.g. `ChatRepository`).
- Implementation in `data/repository/`. Name: `Firebase<Domain>Repository` or `Firestore<Domain>Repository`.
- All public functions return `Result<T>` — wrap Firebase calls in `try { Result.success(…) } catch (e: Exception) { Result.failure(e) }`.
- Bound in `di/RepositoryModule.kt` with `@Binds`.

---

## Model split rules

- **Consumer models** (`domain/model/consumer/`) — data flowing **into** a use case (params). Named after the action: `Login`, `CreateChat`, `Message`, `UserName`.
- **Producer models** (`domain/model/producer/`) — data flowing **out** of a use case (view entities). Named after what they represent: `ChatViewEntity`, `ChatMessageViewEntity`.
- Never use raw `Pair` or `Triple` to pass structured data across layers — define a named data class.

---

## DI rules

- `di/FirebaseModule.kt` — `@Provides` for `FirebaseAuth`, `FirebaseFirestore`, `FirebaseMessaging`.
- `di/RepositoryModule.kt` — `@Binds` for every repository interface → implementation pair.
- Every new repository requires a `@Binds` entry before it can be injected.
- No manual instantiation of repositories, use cases, or ViewModels anywhere in the codebase.

---

## Naming conventions

| Artefact | Convention | Example |
|---|---|---|
| Screen composable | PascalCase + `Screen` / `Route` suffix | `ChatScreen`, `ChatRoute` |
| ViewModel | PascalCase + `ViewModel` | `ChatViewModel` |
| UseCase | PascalCase + `UseCase` | `SendMessageUseCase` |
| Repository interface | PascalCase + `Repository` | `ChatRepository` |
| Repository implementation | `Firebase`/`Firestore` + interface name | `FirestoreChatRepository` |
| Consumer model | PascalCase noun/action | `CreateChat`, `Login` |
| Producer model | PascalCase + `ViewEntity` | `ChatViewEntity` |
| Data DTO | PascalCase + `Response` | `ChatResponse` |
| Mapper file | PascalCase + `Mapper` | `ChatDetailsResponseMapper` |
| Hilt module | PascalCase + `Module` | `RepositoryModule` |
| Feature package | lowercase, no separators | `ui/chat/`, `ui/admin/` |

---

## Enforcement checklist

Run through this before marking any task complete:

- [ ] New file is in the correct layer package (`data` / `domain` / `ui` / `di`).
- [ ] Dependency direction is inward — no layer imports from the layer above it.
- [ ] UseCase extends the correct base class for its data direction.
- [ ] ViewModel extends `BaseViewModel`, exposes `_uiState: MutableStateFlow<UiState>` for loading/error/success and separate `StateFlow` fields for screen data. Uses `SharedFlow` for one-shot navigation events.
- [ ] Navigation is a `SharedFlow` event — not a boolean in `UiState`.
- [ ] Screen is split into `<Feature>Route` (stateful) and `<Feature>Screen` (stateless + preview).
- [ ] Repository returns `Result<T>` — no thrown exceptions crossing layer boundaries.
- [ ] New repository interface is bound in `RepositoryModule`.
- [ ] No Firebase SDK imports outside `data/repository/` and `di/`.
- [ ] Consumer models in `domain/model/consumer/`, producer models in `domain/model/producer/`.
- [ ] No `Pair` or `Triple` used for structured cross-layer data.
- [ ] File and class names match the naming conventions table.
