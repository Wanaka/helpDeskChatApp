# Testing Principles

These principles govern how all tests are written and maintained in this project. They apply to repository tests, ViewModel tests, and use case tests. Test files must follow the same package and naming conventions as production code — see `docs/ai/architecture-principles.md`.

---

## Testing stack

| Purpose | Tool |
|---|---|
| Test runner | JUnit4 (`org.junit.Test`) |
| Mocking | MockK (`io.mockk`) |
| Coroutine testing | `kotlinx-coroutines-test` (`runTest`, `TestDispatcher`) |
| Flow / StateFlow testing | Turbine (`app.cash.turbine.test`) |
| Main dispatcher swap | `MainDispatcherRule` (`app/src/test/.../util/MainDispatcherRule.kt`) |

All tests are **JVM unit tests** under `app/src/test/`. No Robolectric or instrumented (`androidTest`) tests unless unavoidable.

---

## What to test

| Layer | What to cover |
|---|---|
| `data/repository/` | `Result.success` and `Result.failure` paths for every public function |
| `domain/usecase/` | Non-trivial logic, delegation to repository, correct base class behaviour |
| `domain/viewmodel/` | `UiState` transitions on success and failure; one-shot event emission |

Do **not** test:
- Composables / UI rendering — no Compose test dependency is in the project.
- Android framework classes directly — mock at the interface boundary.
- Implementation details (private fields, internal state). Test observable output only.

---

## File placement and naming

Mirror the production package under `app/src/test/java/com/example/helpdeskchatapp/`. Name files `<ClassName>Test.kt`.

```
app/src/test/java/com/example/helpdeskchatapp/
  data/repository/
    FirestoreChatRepositoryTest.kt
    FirebaseUserRepositoryTest.kt
  domain/viewmodel/
    LoginViewModelTest.kt
    ChatViewModelTest.kt
    AdminViewModelTest.kt
  domain/usecase/
    SendMessageUseCaseTest.kt   # only if logic is non-trivial
  util/
    MainDispatcherRule.kt       # shared test utility
```

---

## ViewModel tests

- ViewModels depend on **use cases** — mock them with `mockk()`. Use `mockk(relaxed = true)` for use cases a given test does not assert on.
- Use cases expose `operator fun invoke` (often `suspend`): stub with `coEvery { useCase(any()) } returns …`, verify with `coVerify { useCase(match { … }) }`.
- Add `@get:Rule val mainDispatcherRule = MainDispatcherRule()` to every ViewModel test class.
- **Beware `init {}` blocks** — stub all use cases called in `init` before constructing the ViewModel.
- Prefer `UnconfinedTestDispatcher` in `MainDispatcherRule` so `viewModelScope.launch` runs eagerly.

### Asserting StateFlow

```kotlin
@Test
fun `login success updates uiState to Success`() = runTest {
    coEvery { loginUseCase(any()) } returns Result.success(Unit)
    val viewModel = LoginViewModel(loginUseCase, getCurrentUserUseCase)

    viewModel.login(Login("e@x.com", "pw"))

    assertTrue(viewModel.uiState.value is UiState.Success)
}
```

### Asserting one-shot navigation events (Turbine)

```kotlin
@Test
fun `login success emits navigateToChat event`() = runTest {
    coEvery { loginUseCase(any()) } returns Result.success(Unit)
    val viewModel = LoginViewModel(loginUseCase, getCurrentUserUseCase)

    viewModel.navigateToChat.test {
        viewModel.login(Login("e@x.com", "pw"))
        assertEquals(Unit, awaitItem())
        cancelAndConsumeRemainingEvents()
    }
}
```

### Asserting failure

```kotlin
@Test
fun `login failure updates uiState to Error`() = runTest {
    coEvery { loginUseCase(any()) } returns Result.failure(Exception("Invalid credentials"))
    val viewModel = LoginViewModel(loginUseCase, getCurrentUserUseCase)

    viewModel.login(Login("e@x.com", "wrong"))

    assertTrue(viewModel.uiState.value is UiState.Error)
}
```

---

## Repository tests

Repositories are constructor-injected with Firebase clients — mock those with MockK. No real Firebase, no emulator.

### Mocking Firebase Tasks

Firebase SDK returns `Task<T>`, awaited via `kotlinx.coroutines.tasks.await()`. Use these helpers:

```kotlin
fun <T> succeededTask(value: T): Task<T> = mockk {
    every { isComplete } returns true
    every { isCanceled } returns false
    every { exception } returns null
    every { result } returns value
}

fun <T> failedTask(e: Exception): Task<T> = mockk {
    every { isComplete } returns true
    every { isCanceled } returns false
    every { exception } returns e
}
```

### Mocking Firestore chains

```kotlin
every {
    firestore.collection("chats").document(chatId).get()
} returns succeededTask(mockDocumentSnapshot)
```

### Asserting Result

```kotlin
@Test
fun `getChatForUser returns success when document exists`() = runTest {
    every { firestore.collection("chats").document(chatId).get() } returns succeededTask(snapshot)

    val result = repository.getChatForUser(userId)

    assertTrue(result.isSuccess)
    assertEquals(expectedEntity, result.getOrNull())
}

@Test
fun `getChatForUser returns failure when Firestore throws`() = runTest {
    every { firestore.collection("chats").document(chatId).get() } returns failedTask(Exception("Network error"))

    val result = repository.getChatForUser(userId)

    assertTrue(result.isFailure)
}
```

---

## Test quality rules

- **No real Firebase calls** — no network, no emulator in JVM unit tests.
- **No `Thread.sleep`** — use `runTest`, `advanceUntilIdle()`, or `advanceTimeBy()`.
- **Arrange / Act / Assert** structure in every test, separated by a blank line.
- **One behaviour per `@Test`** — multiple `expect`-style checks are fine, but one clear intent per test.
- **Descriptive test names** — use backtick names: `` `login with invalid email returns failure` ``.
- **Both paths** — always test `Result.success` and `Result.failure` for every repository and use case under test.
- No snapshot tests. No tests of implementation details (private state, internal call order unless critical).
- Tests must be deterministic and pass in CI without local environment dependencies.

---

## README.md ownership

The tester owns `README.md`. It must always reflect the current state of the project.

### Required sections

```markdown
# HelpDesk Chat App

Short one-paragraph description of what this app does and who it is for.

## Tech stack

Bullet list: Android, Kotlin, Jetpack Compose, Hilt, Firebase Auth, Firestore, FCM, Coroutines/Flow, version numbers.

## Getting started

Prerequisites, clone steps, how to open in Android Studio, how to run.

## Project structure

Top-level package overview with a one-line description of each package's purpose.

## Testing

How to run unit tests (`./gradlew testDebugUnitTest`). Where test reports are generated.

## Features

A section per major feature. Each entry: feature name, what it does, which screen it lives on.
```

### README update rules

- **After every new feature** — add an entry to the Features section.
- **After adding a new screen** — document the screen in Project structure or Features.
- **After a stack change** — update the Tech stack section.
- Keep the README factual and concise. No marketing language. No future plans. Only what exists and works now.

---

## Pre-completion checklist

Before marking any testing task done:

- [ ] Every new repository function has a `Result.success` and `Result.failure` test.
- [ ] Every new ViewModel method has a test for each `UiState` outcome.
- [ ] Every new one-shot event has a Turbine assertion test.
- [ ] `MainDispatcherRule` added to every ViewModel test class.
- [ ] Use cases called in `init {}` are stubbed before ViewModel construction.
- [ ] No real Firebase calls in JVM tests — mocked at the repository interface or Firebase client level.
- [ ] No `Thread.sleep` — `runTest` / `advanceUntilIdle()` used instead.
- [ ] Test names are descriptive backtick strings.
- [ ] All tests pass locally: `./gradlew :app:testDebugUnitTest`.
- [ ] README updated: new feature documented, new screens listed.
