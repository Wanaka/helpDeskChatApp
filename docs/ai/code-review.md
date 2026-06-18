# Code Review Principles

Code review is a quality gate, a knowledge-sharing tool, and a design feedback mechanism. These principles define how reviews are conducted in this project — what to look for, how to communicate findings, and what the bar for approval is.

---

## Purpose of a review

A review checks that:
1. The code is **correct** — it does what it claims to do.
2. The code is **consistent** — it follows the architecture and coding principles.
3. The code is **safe** — it introduces no security vulnerabilities or regressions.
4. The code is **maintainable** — a future reader can understand and change it without fear.

A review is not a style debate, a rewrite request, or an opportunity to impose personal preference. If the code works and follows the project principles, it is mergeable.

---

## Before you review

1. Read the PR description. Understand what the change is trying to do and why.
2. Check the linked issue or task for context that may not be in the code.
3. Build and run the app locally if the change is non-trivial or touches critical paths.
4. Read `docs/ai/architecture-principles.md` and `docs/ai/coding-principles.md` — these are your checklist source of truth.

---

## What to check

### Architecture and structure

- Files are in the correct layer package per `docs/ai/architecture-principles.md`.
- Dependency direction flows inward only: `ui` → `domain` → `data/interfaces`.
- No Firebase SDK imports outside `data/repository/` and `di/`.
- No business logic in Composables. No markup in ViewModels or use cases.
- UseCase extends the correct base class (`UseCase`, `ProducerUseCase`, or `ConsumerUseCase`).
- Navigation is a `SharedFlow` one-shot event — not a boolean in `UiState`.
- New repository interface is bound in `RepositoryModule`.

### Correctness

- The code does what the PR description says it does.
- Edge cases are handled: empty state, null/loading, error state, `Result.failure` path.
- No missing `await` on suspend functions, no unhandled coroutine exceptions.
- No silent failures — errors are surfaced via `UiState.Error` or `_toastEvent`.

### Kotlin and Android

- No `!!` (non-null assertion) — use safe calls, `let`, or explicit null checks.
- No `any` types — use explicit generics or sealed classes.
- No hardcoded strings — use string resources where user-visible text appears.
- `viewModelScope.launch` used for coroutines in ViewModels — no `GlobalScope`.
- `collectAsStateWithLifecycle()` used in Composables — not `collectAsState()`.
- Lifecycle-aware collection in `LaunchedEffect` for one-shot events.

### Result-based error handling

- Repository functions return `Result<T>` — no thrown exceptions crossing layer boundaries.
- ViewModels fold the `Result` into `UiState` — no `.getOrThrow()` in ViewModels or UI.
- Use cases pass `Result` through without swallowing failures.

### Compose UI

- Screen is split into `<Feature>Route` (stateful) and `<Feature>Screen` (stateless + `@Preview`).
- `StateHandler` composable used for `UiState` branches — no duplicated Loading/Error/Success switch per screen.
- No side effects in the `@Composable` body outside of `LaunchedEffect`, `SideEffect`, or `DisposableEffect`.
- No business logic inside `@Composable` functions.

### Code quality

- No duplicated logic — DRY applied. Same block appearing twice is a flag.
- No dead code or commented-out blocks.
- No unnecessary complexity — KISS applied.
- No features built for hypothetical future requirements — YAGNI applied.
- Consumer models in `domain/model/consumer/`, producer models in `domain/model/producer/`. No raw `Pair` or `Triple` for structured data.

### Security

- No secrets, API keys, or tokens hardcoded in source files.
- User input validated before use in Firestore queries.
- No direct string concatenation into Firestore paths from user input.

### Tests

- New repository and ViewModel code has corresponding unit tests per `docs/ai/testing-principles.md`.
- Tests assert `Result.success` and `Result.failure` paths.
- No real Firebase calls in JVM unit tests — mocked at the repository interface boundary.

---

## How to write feedback

### Use clear severity labels

Prefix every comment with one of:

| Label | Meaning |
|---|---|
| **blocking:** | Must be fixed before merge. Incorrect, unsafe, or violates a core principle. |
| **suggestion:** | Worth improving but not a blocker. The author can decide. |
| **question:** | Genuinely unclear — ask before assuming it is wrong. |
| **nit:** | Minor style point. Completely optional. |

### Be specific and actionable

Every blocking comment must include:
- What is wrong (the rule it violates or the risk it introduces).
- Where it is wrong (file path and line reference).
- What the fix should be (a concrete suggestion or example).

```
// good
blocking: `FirestoreChatRepository.kt:34` — Firebase exception thrown directly to caller.
Per architecture-principles.md §Repository conventions, all repository functions must return
Result<T>. Wrap the Firebase call: try { Result.success(...) } catch (e: Exception) { Result.failure(e) }

// bad
Don't throw here.
```

### Tone

- Review the code, not the author.
- Use "this" not "you": "this function does X" not "you wrote X".
- Assume good intent. Ask before labelling something wrong.
- If something is well done, say so — positive feedback is part of the review.

---

## Approval criteria

Approve when:
- All blocking comments are resolved.
- The change does what the PR description says.
- No architecture, security, or correctness issues remain.
- Tests cover the new behaviour.

Do not block on:
- Personal style preferences not covered by the project principles.
- Suggestions the author has considered and consciously declined (note the decision in the PR).
- Hypothetical future scenarios — YAGNI applies to review comments too.

---

## PR author responsibilities

Before requesting review:

- [ ] Self-review your own diff first. Read every line as if you were the reviewer.
- [ ] All items on the developer pre-completion checklist in `docs/ai/coding-principles.md` are done.
- [ ] All items on the tester pre-completion checklist in `docs/ai/testing-principles.md` are done.
- [ ] CI is green on the branch (`./gradlew lintDebug testDebugUnitTest assembleDebug`).
- [ ] PR description explains what changed and why — not just what the code does.
- [ ] Breaking changes or Firestore schema changes are called out explicitly.
- [ ] Screenshots attached for any UI changes.

---

## Resolving disagreements

If author and reviewer disagree on a non-blocking point:
1. The author documents their reasoning as a PR comment.
2. If it involves an architecture decision, escalate to the architect agent.
3. The decision is noted in the PR — not left as an unresolved thread.

Blocking comments that reference a specific rule in `docs/ai/` are not negotiable until the rule itself is changed. If you think the rule is wrong, open a separate PR to update the principle document.
