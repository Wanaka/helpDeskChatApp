# CI Setup

This document describes the GitHub Actions CI/CD pipeline for this project — what runs, when it runs, and the rules for keeping it working as the codebase evolves.

---

## Overview

Two workflow files live under `.github/workflows/`:

| File | Trigger | Purpose |
|---|---|---|
| `ci.yml` | Push to `master` and `feature/**` branches | Fast feedback — lint, unit tests, assemble debug |
| `pr.yml` | Pull request targeting `master` | Full merge gate — everything in ci.yml + stricter checks |

---

## Build facts (verify before changing)

- Gradle **wrapper** present (`./gradlew`); pinned in `gradle/wrapper/gradle-wrapper.properties` (Gradle 9.x). Always use the wrapper, never a system Gradle.
- **JDK 17** required (`compileOptions` + `jvmToolchain(17)`, AGP 9.x). Use `actions/setup-java` with `temurin` 17.
- `google-services.json` **is committed** at `app/google-services.json` — builds work in CI without secrets.
- Module under test: `:app`. Dependency versions live in `gradle/libs.versions.toml`.

---

## Required triggers

```yaml
# ci.yml
on:
  push:
    branches: [ master, 'feature/**' ]

# pr.yml
on:
  pull_request:
    branches: [ master ]
    types: [ opened, synchronize, reopened ]
```

---

## Pipeline steps

A single `ubuntu-latest` job is appropriate for this app size.

1. `actions/checkout@v4`
2. `actions/setup-java@v4` (distribution: `temurin`, java-version: `17`)
3. `gradle/actions/setup-gradle@v4` — dependency and build caching
4. `chmod +x ./gradlew`
5. Run with `--no-daemon`:
   - `./gradlew lintDebug` — Android Lint
   - `./gradlew testDebugUnitTest` — JVM unit tests (repos + ViewModels)
   - `./gradlew assembleDebug` — smoke-build the APK
6. Upload artifacts with `if: always()`:
   - Unit test reports: `app/build/reports/tests/`, `app/build/test-results/`
   - Lint report: `app/build/reports/lint-results-debug.*`
   - Debug APK: `app/build/outputs/apk/debug/*.apk`

---

## Job dependency graph

```
lint ──────────────┐
                   ├──→ unit-tests
                   └──→ build (assembleDebug)
```

`lint` runs first; `unit-tests` and `build` each wait for it to pass. A single job is also acceptable for this app size.

---

## Branch protection rules

Configure the following on `master` in GitHub (Settings → Branches → Add rule):

| Setting | Value |
|---|---|
| Require a pull request before merging | ✅ enabled |
| Require approvals | 1 (minimum) |
| Require status checks to pass before merging | ✅ enabled |
| Required status checks | `Lint`, `Unit tests`, `Build` |
| Require branches to be up to date before merging | ✅ enabled |
| Do not allow bypassing the above settings | ✅ enabled |

> Status check names must exactly match the `name:` field of the corresponding workflow job.

---

## Workflow maintenance rules

- **Pin action versions** — use `@v4` major-version tags, never `@main` or `@latest`.
- **Concurrency groups** — both workflows set `concurrency` with `cancel-in-progress: true` to cancel stale runs.
- **`if: always()`** on all artifact upload steps so reports survive test failures.
- **No secrets hardcoded** — use `${{ secrets.* }}`. The committed `google-services.json` is intentional.
- **Adding a new check** — add the Gradle task first, confirm it passes locally (`./gradlew tasks --all | grep <task>`), then add it to both `ci.yml` and `pr.yml`, update the branch protection required checks list, and document the change in `README.md`.
- **Don't add steps that need secrets the repo doesn't have.** If you introduce one (signing key, Firebase service account), reference it via `${{ secrets.* }}` and document the required secret in the PR.

---

## Pre-completion checklist

Before marking any CI task done:

- [ ] YAML syntax is valid (use a linter or `act` locally).
- [ ] All Gradle tasks referenced in the workflow exist (`./gradlew tasks --all | grep <task>`).
- [ ] Action versions are pinned to `@v4` major tags.
- [ ] `concurrency` + `cancel-in-progress: true` is set on both workflows.
- [ ] Artifact uploads use `if: always()`.
- [ ] No hardcoded secrets or API keys in workflow YAML.
- [ ] Branch protection required checks updated if new jobs were added.
- [ ] `README.md` updated if CI environment or setup steps changed.
