# Storage Module Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add optional `:storage` KMP Gradle module with `StorageClient` facade, platform SAF / iOS document providers, app-supplied `StoragePickerHost`, NoOp when disabled, and a README mirroring `:billing` (including no runtime permissions, picker host wiring, and compile verification).

**Architecture:** Thin `StorageClient` delegates CRUD to swappable `StorageProvider` and folder pick to `StoragePickerHost`. `NoOpStorageProvider` when `StorageConfig.enabled = false`. Platform defaults via `expect`/`actual` module wiring — `jvmMain` stubs for JVM unit tests. No `:domain` / `:data` changes. **Do not** add `implementation(projects.storage)` to `:shared` or wire Koin in app entry by default. **Do not** add Android/iOS runtime storage permissions.

**Tech Stack:** Kotlin Multiplatform, Koin, Kotlin Coroutines, kotlin-test (commonTest on JVM), Android `androidx.documentfile:documentfile`, iOS `NSFileManager` + security-scoped bookmarks.

**Spec:** [`docs/superpowers/specs/2026-06-30-storage-module-design.md`](../specs/2026-06-30-storage-module-design.md)

---

## File map

| File | Responsibility |
|------|----------------|
| `storage/build.gradle.kts` | KMP module; documentfile on androidMain; jvm for tests |
| `storage/README.md` | Integration guide — picker hosts, permissions (none), verify checklist |
| `storage/src/commonMain/.../api/StorageModels.kt` | Public models + results |
| `storage/src/commonMain/.../api/StorageClient.kt` | Facade interface |
| `storage/src/commonMain/.../api/StorageConfig.kt` | Init config |
| `storage/src/commonMain/.../api/provider/StorageProvider.kt` | I/O contract |
| `storage/src/commonMain/.../api/provider/StoragePickerHost.kt` | Picker bridge contract |
| `storage/src/commonMain/.../api/StorageFeatureModule.kt` | Public Koin entry |
| `storage/src/commonMain/.../impl/StorageClientImpl.kt` | Facade impl |
| `storage/src/commonMain/.../impl/StorageModule.kt` | Koin + provider selection |
| `storage/src/commonMain/.../impl/StoragePathValidator.kt` | Relative path validation |
| `storage/src/commonMain/.../impl/provider/NoOpStorageProvider.kt` | Disabled / JVM I/O stub |
| `storage/src/androidMain/.../impl/AndroidSafStorageProvider.kt` | SAF CRUD |
| `storage/src/androidMain/.../impl/SafUriCodec.kt` | Token ↔ Uri helpers |
| `storage/src/androidMain/.../impl/StorageModule.android.kt` | `actual` default provider |
| `storage/src/iosMain/.../impl/IosDocumentStorageProvider.kt` | Scoped URL CRUD |
| `storage/src/iosMain/.../impl/SecurityScopedBookmarkCodec.kt` | Token ↔ bookmark |
| `storage/src/iosMain/.../impl/StorageModule.ios.kt` | `actual` default provider |
| `storage/src/jvmMain/.../impl/StorageModule.jvm.kt` | `actual` → NoOp |
| `storage/src/commonTest/.../api/StorageModelsTest.kt` | Model smoke tests |
| `storage/src/commonTest/.../impl/StoragePathValidatorTest.kt` | Path rules |
| `storage/src/commonTest/.../impl/StorageClientImplTest.kt` | Facade + fakes |
| `storage/src/commonTest/.../impl/provider/NoOpStorageProviderTest.kt` | NoOp behavior |
| `settings.gradle.kts` | `include(":storage")` |
| `build.gradle.kts` | Add `:storage:allTests` to `qualityCheck` |
| `architecture/.../LayerDependencyTest.kt` | `domain` / `data` must not import `:storage` |

**Documented in README only (optional integration):**

| File | Responsibility |
|------|----------------|
| `androidApp/.../storage/AndroidStoragePickerHost.kt` | `OpenDocumentTree` + persistable URI flags |
| `shared/iosMain/.../storage/IosStoragePickerHost.kt` | UIDocumentPicker bridge |
| `shared/build.gradle.kts` | `implementation(projects.storage)` when feature needs it |
| `MyDayApplication.kt` / `KoinIos.kt` | `storageFeatureModule(...)` registration |

---

### Task 1: Gradle module scaffold

**Files:**
- Create: `storage/build.gradle.kts`
- Modify: `settings.gradle.kts`
- Modify: `build.gradle.kts` (`qualityCheck`)

- [ ] **Step 1: Register module**

In `settings.gradle.kts` after `include(":billing")`:

```kotlin
include(":storage")
```

- [ ] **Step 2: Create `storage/build.gradle.kts`**

Mirror `billing/build.gradle.kts` structure:

```kotlin
@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
}

group = "com.devindie.myday"

detekt {
    source.setFrom(
        "src/commonMain/kotlin",
        "src/androidMain/kotlin",
        "src/iosMain/kotlin",
        "src/jvmMain/kotlin",
    )
}

kotlin {
    jvm {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }
    android {
        namespace = "com.devindie.myday.storage"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.androidx.documentfile)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

tasks.matching { task ->
    task.name.contains("ios", ignoreCase = true) && task.name.contains("Test", ignoreCase = true)
}.configureEach {
    enabled = false
}
```

Note: `androidx-documentfile` already exists in `gradle/libs.versions.toml` — no catalog change required.

- [ ] **Step 3: Add to qualityCheck**

In root `build.gradle.kts`, add `":storage:allTests"` to `qualityCheck` `dependsOn` (after `:billing:allTests`).

- [ ] **Step 4: Verify scaffold**

Run: `./gradlew :storage:compileKotlinJvm`

Expected: `BUILD SUCCESSFUL` once a placeholder file exists (Task 2 adds real sources).

- [ ] **Step 5: Commit**

```bash
git add settings.gradle.kts build.gradle.kts storage/
git commit -m "chore: scaffold :storage KMP module"
```

---

### Task 2: Public models + provider contracts

**Files:**
- Create: `storage/src/commonMain/.../api/StorageModels.kt`
- Create: `storage/src/commonMain/.../api/provider/StorageProvider.kt`
- Create: `storage/src/commonMain/.../api/provider/StoragePickerHost.kt`
- Create: `storage/src/commonTest/.../api/StorageModelsTest.kt`

- [ ] **Step 1: Write failing test**

```kotlin
package com.devindie.myday.storage.api

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StorageModelsTest {
    @Test
    fun storageLocationToken_wrapsOpaqueString() {
        val token = StorageLocationToken("tree-uri-or-bookmark")
        assertEquals("tree-uri-or-bookmark", token.value)
    }

    @Test
    fun storageResult_successWrapsValue() {
        val result = StorageResult.Success("ok")
        assertEquals("ok", (result as StorageResult.Success).value)
    }

    @Test
    fun storageAccessMode_hasReadWrite() {
        assertTrue(StorageAccessMode.entries.contains(StorageAccessMode.ReadWrite))
    }
}
```

- [ ] **Step 2: Run test — expect FAIL**

Run: `./gradlew :storage:cleanJvmTest :storage:jvmTest --tests "com.devindie.myday.storage.api.StorageModelsTest"`

- [ ] **Step 3: Implement models + contracts**

Implement types from spec (`StorageAccessMode`, `StoragePickRequest`, `StorageLocationToken`, `StorageEntry`, `StorageResult`, `StorageError`, `StorageProvider`, `StoragePickerHost`).

- [ ] **Step 4: Run test — expect PASS**

- [ ] **Step 5: Commit**

```bash
git commit -m "feat(storage): add public models and provider contracts"
```

---

### Task 3: Path validator + NoOp provider

**Files:**
- Create: `storage/src/commonMain/.../impl/StoragePathValidator.kt`
- Create: `storage/src/commonMain/.../impl/provider/NoOpStorageProvider.kt`
- Create: `storage/src/commonTest/.../impl/StoragePathValidatorTest.kt`
- Create: `storage/src/commonTest/.../impl/provider/NoOpStorageProviderTest.kt`

- [ ] **Step 1: Write failing path validator tests**

Cases:
- `""` → valid (root)
- `"notes/readme.md"` → valid
- `"../secret"` → invalid
- `"/leading"` → invalid
- `"a//b"` → invalid

- [ ] **Step 2: Implement `StoragePathValidator`**

```kotlin
internal object StoragePathValidator {
    fun validate(relativePath: String): StorageError.InvalidPath? { /* … */ }
}
```

- [ ] **Step 3: Write failing NoOp tests**

All CRUD methods return `Failure(NotConfigured)`.

- [ ] **Step 4: Implement `NoOpStorageProvider`**

- [ ] **Step 5: Run tests — expect PASS**

Run: `./gradlew :storage:jvmTest`

- [ ] **Step 6: Commit**

---

### Task 4: StorageClient facade + impl + Koin

**Files:**
- Create: `storage/src/commonMain/.../api/StorageClient.kt`
- Create: `storage/src/commonMain/.../api/StorageConfig.kt`
- Create: `storage/src/commonMain/.../api/StorageFeatureModule.kt`
- Create: `storage/src/commonMain/.../impl/StorageClientImpl.kt`
- Create: `storage/src/modules/Modules.kt` → actually `StorageModule.kt`
- Create: `storage/src/jvmMain/.../impl/StorageModule.jvm.kt`
- Create: `storage/src/commonTest/.../impl/StorageClientImplTest.kt`

- [ ] **Step 1: Write failing `StorageClientImplTest`**

Use manual fakes:

```kotlin
private class FakeStorageProvider : StorageProvider { /* in-memory map */ }
private class FakeStoragePickerHost : StoragePickerHost {
    override suspend fun pickFolder(request: StoragePickRequest) =
        StorageResult.Success(StorageLocationToken("fake-root"))
}
```

Test cases:
- `pickFolder` delegates to host
- `readText` UTF-8 round-trip via bytes
- Invalid path short-circuits without calling provider
- Provider exception → `StorageError.Io`
- Disabled config uses NoOp (via module test or direct impl with NoOp provider)

- [ ] **Step 2: Implement `StorageClient` + `StorageClientImpl`**

- [ ] **Step 3: Implement Koin module**

```kotlin
// api/StorageFeatureModule.kt
fun storageFeatureModule(config: StorageConfig): Module = createStorageModule(config)

// impl/StorageModule.kt
internal expect fun createPlatformStorageProvider(): StorageProvider

internal fun createStorageModule(config: StorageConfig): Module = module {
    single<StorageClient> {
        val provider = when {
            !config.enabled -> NoOpStorageProvider()
            config.provider != null -> config.provider
            else -> createPlatformStorageProvider()
        }
        StorageClientImpl(
            provider = provider,
            pickerHost = config.pickerHost,
            enabled = config.enabled,
        )
    }
}
```

- [ ] **Step 4: JVM actual**

```kotlin
// jvmMain/StorageModule.jvm.kt
internal actual fun createPlatformStorageProvider(): StorageProvider = NoOpStorageProvider()
```

- [ ] **Step 5: Run tests — expect PASS**

- [ ] **Step 6: Commit**

---

### Task 5: Android SAF provider

**Files:**
- Create: `storage/src/androidMain/.../impl/AndroidSafStorageProvider.kt`
- Create: `storage/src/androidMain/.../impl/SafUriCodec.kt`
- Create: `storage/src/androidMain/.../impl/StorageModule.android.kt`

- [ ] **Step 1: Implement `SafUriCodec`**

- Parse `StorageLocationToken.value` as tree `Uri`
- Invalid token → `PermissionDenied`

- [ ] **Step 2: Implement `AndroidSafStorageProvider`**

Operations via `DocumentFile`:
- `list`: immediate children only
- `exists`: file or directory at path
- `readBytes`: `ContentResolver.openInputStream`
- `writeBytes`: openOutputStream with truncate; fail if write not granted
- `delete`: `DocumentFile.delete()`

Use `withContext(Dispatchers.IO)` for blocking I/O.

- [ ] **Step 3: Android actual module wiring**

```kotlin
internal actual fun createPlatformStorageProvider(): StorageProvider =
    AndroidSafStorageProvider(appContext = get()) // inject Context via Koin androidContext
```

Pass `Context` through Koin `androidContext()` — document in README that app must call `androidContext()` in `startKoinApp`.

- [ ] **Step 4: Compile Android**

Run: `./gradlew :storage:compileDebugKotlinAndroid`

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 5: Commit**

---

### Task 6: iOS document provider

**Files:**
- Create: `storage/src/iosMain/.../impl/IosDocumentStorageProvider.kt`
- Create: `storage/src/iosMain/.../impl/SecurityScopedBookmarkCodec.kt`
- Create: `storage/src/iosMain/.../impl/StorageModule.ios.kt`

- [ ] **Step 1: Implement bookmark codec**

- Decode base64 token → `NSURL` with security scope
- Stale bookmark → `PermissionDenied`

- [ ] **Step 2: Implement CRUD with scoped access**

Pattern:

```kotlin
private inline fun <T> withScopedAccess(url: NSURL, block: () -> T): T {
    val started = url.startAccessingSecurityScopedResource()
    try {
        return block()
    } finally {
        if (started) url.stopAccessingSecurityScopedResource()
    }
}
```

- [ ] **Step 3: iOS actual module wiring**

- [ ] **Step 4: Compile iOS**

Run: `./gradlew :storage:compileKotlinIosSimulatorArm64`

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 5: Commit**

---

### Task 7: Architecture tests

**Files:**
- Modify: `architecture/src/test/kotlin/com/devindie/myday/architecture/layer/LayerDependencyTest.kt`

- [ ] **Step 1: Add Konsist tests** (mirror billing)

```kotlin
@Test
fun `domain layer does not import storage`() {
    Konsist.scopeFromProduction()
        .files
        .filter { it.path.contains("/domain/") }
        .assertFalse { file ->
            file.imports.any { import ->
                import.name.startsWith("com.devindie.myday.storage.")
            }
        }
}

@Test
fun `data layer does not import storage`() {
    // same pattern for /data/
}
```

- [ ] **Step 2: Run architecture tests**

Run: `./gradlew :architecture:test`

Expected: PASS

- [ ] **Step 3: Commit**

---

### Task 8: README + integration guide

**Files:**
- Create: `storage/README.md`

- [ ] **Step 1: Write README** (mirror `billing/README.md` sections)

Required sections:
1. What you get (API table)
2. **Permissions — none required** (explicit callout)
3. Step 1 — Gradle module
4. Step 2 — Android picker host (`OpenDocumentTree`, `takePersistableUriPermission`, flag mapping table for `StorageAccessMode`)
5. Step 3 — iOS picker host (bookmark creation)
6. Step 4 — Koin wiring
7. Step 5 — ViewModel usage example
8. Step 6 — Verify (`:storage:allTests`, iOS compile)
9. Setup checklist
10. Module layout
11. Troubleshooting (`PermissionDenied`, stale token, write without ReadWrite pick)

**Flag mapping table (Android):**

| `StorageAccessMode` | `takePersistableUriPermission` flags |
|---------------------|--------------------------------------|
| `Read` | `FLAG_GRANT_READ_URI_PERMISSION` |
| `Write` | `FLAG_GRANT_WRITE_URI_PERMISSION` |
| `ReadWrite` | read + write flags |

- [ ] **Step 2: Commit**

```bash
git commit -m "docs(storage): add README and integration guide"
```

---

### Task 9: Optional app shell integration (demo)

**Only when a feature needs storage — skip for template default.**

**Files:**
- Create: `androidApp/src/main/kotlin/.../storage/AndroidStoragePickerHost.kt`
- Create: `shared/src/iosMain/kotlin/.../storage/IosStoragePickerHost.kt`
- Modify: `shared/build.gradle.kts`, `androidApp/build.gradle.kts`, Koin entry points

- [ ] **Step 1: Android picker host**

- Register `ActivityResultContracts.OpenDocumentTree()` on `MainActivity` or inject launcher registry
- Map `StorageAccessMode` → persistable URI flags
- Suspend until result (`CompletableDeferred` or callback flow)

- [ ] **Step 2: iOS picker host**

- Bridge to UIKit document picker; return bookmark token

- [ ] **Step 3: Wire Koin**

```kotlin
storageFeatureModule(
    StorageConfig(
        enabled = true,
        pickerHost = androidStoragePickerHost, // platform-specific module or expect/actual holder
    ),
)
```

- [ ] **Step 4: Manual smoke test**

- Pick folder → write file → read back → delete
- Confirm no permission dialogs appear (only system picker)

- [ ] **Step 5: Commit** (if integrated)

---

### Task 10: Final verification

- [ ] Run full quality gate:

```bash
./gradlew :storage:allTests
./gradlew :storage:compileKotlinIosSimulatorArm64
./gradlew :architecture:test
./gradlew qualityCheck
```

Expected: all green.

- [ ] Confirm `AndroidManifest.xml` has **no new storage permissions**

- [ ] Confirm spec + plan docs match implemented API

- [ ] Final commit (if not already committed per task):

```bash
git commit -m "feat(storage): complete StorageClient module with SAF and iOS providers"
```

---

## Android picker host reference (for Task 9)

```kotlin
class AndroidStoragePickerHost(
    private val launcher: suspend (StorageAccessMode) -> Uri?,
) : StoragePickerHost {
    override suspend fun pickFolder(request: StoragePickRequest): StorageResult<StorageLocationToken> {
        val uri = launcher(request.accessMode) ?: return StorageResult.Cancelled
        return StorageResult.Success(StorageLocationToken(uri.toString()))
    }
}

// In MainActivity — after pick, before returning token:
fun persistTreeGrant(uri: Uri, mode: StorageAccessMode) {
    val flags = when (mode) {
        StorageAccessMode.Read -> Intent.FLAG_GRANT_READ_URI_PERMISSION
        StorageAccessMode.Write -> Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        StorageAccessMode.ReadWrite ->
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    }
    contentResolver.takePersistableUriPermission(uri, flags)
}
```

---

## Test helpers (commonTest)

```kotlin
class FakeStorageProvider(
    private val files: MutableMap<String, ByteArray> = mutableMapOf(),
) : StorageProvider {
    // implement against in-memory map keyed by relativePath
}

class FakeStoragePickerHost(
    private val token: StorageLocationToken = StorageLocationToken("fake"),
) : StoragePickerHost {
    override suspend fun pickFolder(request: StoragePickRequest) =
        StorageResult.Success(token)
}
```

---

## Troubleshooting (implementation)

| Issue | Fix |
|-------|-----|
| Koin `Context` missing on Android | Ensure `androidContext()` in `startKoinApp` |
| Write fails after pick | Pick with `ReadWrite`; verify `takePersistableUriPermission` write flag |
| iOS `PermissionDenied` immediately | Bookmark not security-scoped; recreate from picker |
| `:domain` imports storage | Move usage to ViewModel; keep domain pure |
| Accidental permission added | Remove — not required for SAF/document picker v1 |
