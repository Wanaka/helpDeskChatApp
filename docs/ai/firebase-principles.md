# Firebase Principles

This document defines how Firebase is used in this project. All agents and developers must follow these conventions when writing, reviewing, or testing Firebase-related code.

---

## Project details

| Property | Value |
|---|---|
| Project ID | `helpdeskchatapp` |
| Package name | `haag.your.next.developer` |
| `google-services.json` location | `app/google-services.json` (committed intentionally) |

This project connects to an **existing** Firebase project shared with an Android app. Do not modify collection names, document schemas, or security rules without coordinating with the Android app team.

---

## Services in use

| Service | Purpose |
|---|---|
| Firebase Auth | Email/password login + anonymous (guest) login |
| Cloud Firestore | Read and write all application data |
| Firebase Cloud Messaging (FCM) | Push notifications; token stored in Firestore |

No other Firebase services (Storage, Remote Config, Hosting, etc.) are active at this time.

---

## File ownership

Firebase code follows the four-layer architecture from `docs/ai/architecture-principles.md`:

| File | Responsibility |
|---|---|
| `di/FirebaseModule.kt` | Hilt module — the only place Firebase instances are provided (`@Provides`) |
| `data/interfaces/UserRepository.kt` | Auth operations interface |
| `data/interfaces/ChatRepository.kt` | Chat Firestore operations interface |
| `data/interfaces/AdminRepository.kt` | Admin Firestore operations interface |
| `data/repository/FirebaseUserRepository.kt` | Auth implementation: email sign-in, anonymous sign-in, sign-out, current user |
| `data/repository/FirestoreChatRepository.kt` | Firestore chat implementation |
| `data/repository/FirestoreAdminRepository.kt` | Firestore admin implementation |
| `service/MyFirebaseMessagingService.kt` | FCM token refresh and incoming push handling |

### Rules

- `di/FirebaseModule.kt` is the only place `FirebaseAuth`, `FirebaseFirestore`, and `FirebaseMessaging` are instantiated. All repositories receive them via constructor injection.
- Repository implementations are the only files that import `com.google.firebase.*`.
- Use cases, ViewModels, and Composables must not import from `com.google.firebase.*`.
- `MyFirebaseMessagingService` invokes `UpdateFcmTokenUseCase` — it does not write to Firestore directly.

---

## Authentication patterns

### Email login

```kotlin
// Use case layer
class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) : UseCase<Login, Result<Unit>>() {
    override suspend fun invoke(params: Login) = userRepository.login(params.email, params.password)
}
```

### Anonymous login

```kotlin
class LoginAnonymouslyUseCase @Inject constructor(
    private val userRepository: UserRepository
) : ProducerUseCase<Result<Unit>>() {
    override suspend fun invoke() = userRepository.loginAnonymously()
}
```

### Observing auth state

Auth state is obtained via `GetCurrentUserUseCase` — called in ViewModel `init` or on demand. There is no reactive auth state listener exposed to the UI layer; the ViewModel drives navigation on login/logout success.

### Anonymous users

Anonymous users are real Firebase users with a UID. Treat them identically to email users in Firestore writes. Check `IsAnonymousUseCase` when gating features that require a permanent account.

---

## Firestore patterns

### Wrapping Firebase Tasks

All Firestore and Auth calls return `Task<T>`, awaited via `kotlinx.coroutines.tasks.await()`. Wrap every call in `Result`:

```kotlin
override suspend fun sendMessage(message: Message): Result<Unit> = try {
    firestore.collection("chats")
        .document(message.chatId)
        .collection("messages")
        .add(message.toMap())
        .await()
    Result.success(Unit)
} catch (e: Exception) {
    Result.failure(e)
}
```

### Mapping DTOs to domain models

Map Firestore `DocumentSnapshot` → data DTO → domain producer model inside the repository using mappers from `data/mapper/`:

```kotlin
val dto = snapshot.toObject(ChatResponse::class.java) ?: return Result.failure(Exception("Not found"))
val entity = ChatDetailsResponseMapper.map(dto)
Result.success(entity)
```

### Firestore collections

Document the collections used by this app as they are added. Do not rename or restructure them without coordinating with the Android app team.

| Collection | Description |
|---|---|
| `users` | User profiles; FCM token stored here |
| `chats` | Chat room documents |
| `chats/{chatId}/messages` | Messages subcollection |
| _(add as you use them)_ | |

---

## FCM token management

- Token refresh is handled in `MyFirebaseMessagingService.onNewToken`.
- The new token is persisted by invoking `UpdateFcmTokenUseCase` — the service must not write to Firestore directly.
- On first login (email or anonymous), `UpdateFcmTokenUseCase` is called to ensure the token is up to date.

---

## Security rules

Security rules live in the Firebase Console / in the Android project's `firestore.rules` file. This project does not own the rules. If a Firestore operation is denied:

1. Check the rules in the Firebase Console under Firestore → Rules.
2. Do not work around rules by skipping auth — fix the rule with the Android team.
3. Do not change collection names or document paths to bypass rules.

---

## CLI usage

Always use `npx -y firebase-tools@latest` — never the naked `firebase` command.

```bash
# Check active project
npx -y firebase-tools@latest use

# List Android apps
npx -y firebase-tools@latest apps:list ANDROID --project helpdeskchatapp

# View Firestore rules
npx -y firebase-tools@latest firestore:rules --project helpdeskchatapp
```

---

## Testing Firebase code

- Unit tests mock repository **interfaces** with MockK — never mock `FirebaseAuth` or `FirebaseFirestore` in ViewModel tests.
- Repository unit tests may mock `FirebaseAuth` / `FirebaseFirestore` directly using the `succeededTask` / `failedTask` helper pattern (see `docs/ai/testing-principles.md`).
- No real Firebase network calls in JVM unit tests.
- `google-services.json` is committed, so CI builds pass without injecting secrets.

---

## Pre-completion checklist

Before marking any Firebase-related task done:

- [ ] Firebase SDK imported only in `data/repository/` and `di/FirebaseModule.kt`.
- [ ] No Firebase imports in use cases, ViewModels, or Composables.
- [ ] All repository functions return `Result<T>` — no thrown Firebase exceptions crossing layer boundaries.
- [ ] New repository interface bound in `di/RepositoryModule.kt`.
- [ ] New Firestore collections documented in the table above.
- [ ] FCM token updates go through `UpdateFcmTokenUseCase` — not direct Firestore writes from the service.
- [ ] `isLoading` / `UiState.Loading` handled in any UI that depends on auth or Firestore data.
- [ ] Unit tests mock the repository interface — no real Firebase calls in Vitest or JVM tests.
