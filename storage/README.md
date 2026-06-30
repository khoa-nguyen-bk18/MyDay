# `:storage` module

Standalone KMP module for **user-picked folder** access and file CRUD via Android Storage Access Framework (SAF) and the iOS document picker. Features inject `StorageClient` from the public `api` package when they need scoped file I/O.

**Public API:** `com.devindie.myday.storage.api`  
**Internal impl:** `com.devindie.myday.storage.impl` (do not import from app code)

**Design spec:** [`docs/superpowers/specs/2026-06-30-storage-module-design.md`](../docs/superpowers/specs/2026-06-30-storage-module-design.md)

---

## What you get

| Capability | API |
|------------|-----|
| Pick folder (READ / WRITE / READ_WRITE) | `StorageClient.pickFolder(StoragePickRequest(...))` |
| List immediate children | `StorageClient.list(token, relativePath)` |
| Read file | `StorageClient.readText` / `readBytes` |
| Write file | `StorageClient.writeText` / `writeBytes` |
| Delete file | `StorageClient.delete` |
| Check existence | `StorageClient.exists` |
| Disable all storage | `StorageConfig(enabled = false)` at Koin init |
| Swap I/O backend | Custom `StorageProvider` in config (tests / future backends) |

---

## Permissions — none required

v1 uses **only** the system folder/document picker. The user grant replaces runtime storage permissions.

- **Do not** add `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, or `MANAGE_EXTERNAL_STORAGE` to `AndroidManifest.xml` for this module.
- **Do not** call `requestPermissions()` for storage before picking.
- Android: persist access with `takePersistableUriPermission()` after `OpenDocumentTree`.
- iOS: use security-scoped bookmarks from the document picker; the module resolves them at CRUD time.

If access is revoked in system settings or a bookmark goes stale, CRUD returns `StorageError.PermissionDenied` — not a missing runtime permission.

---

## Step 1 — Add the Gradle module

### 1.1 Register the module

In `settings.gradle.kts`:

```kotlin
include(":storage")
```

### 1.2 Depend on it when you need storage

In `shared/build.gradle.kts` (only when a feature uses `StorageClient`):

```kotlin
commonMain.dependencies {
    implementation(projects.storage)
}
```

In `androidApp/build.gradle.kts`:

```kotlin
implementation(projects.storage)
```

---

## Step 2 — Android picker host

Implement `StoragePickerHost` in **`androidApp`** (requires `Activity` / `ActivityResultRegistry`).

```kotlin
import android.content.Intent
import android.net.Uri
import com.devindie.myday.storage.api.StorageAccessMode
import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StoragePickRequest
import com.devindie.myday.storage.api.StorageResult
import com.devindie.myday.storage.api.provider.StoragePickerHost

class AndroidStoragePickerHost(
    private val launcher: suspend (StorageAccessMode) -> Uri?,
) : StoragePickerHost {
    override suspend fun pickFolder(request: StoragePickRequest): StorageResult<StorageLocationToken> {
        val uri = launcher(request.accessMode) ?: return StorageResult.Cancelled
        return StorageResult.Success(StorageLocationToken(uri.toString()))
    }
}
```

After the user picks a tree, **persist the grant** before returning the token:

```kotlin
fun persistTreeGrant(contentResolver: ContentResolver, uri: Uri, mode: StorageAccessMode) {
    val flags =
        when (mode) {
            StorageAccessMode.Read -> Intent.FLAG_GRANT_READ_URI_PERMISSION
            StorageAccessMode.Write -> Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            StorageAccessMode.ReadWrite ->
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
    contentResolver.takePersistableUriPermission(uri, flags)
}
```

Register `ActivityResultContracts.OpenDocumentTree()` on your `ComponentActivity` and bridge the suspend launcher (e.g. `CompletableDeferred`).

### Android flag mapping

| `StorageAccessMode` | `takePersistableUriPermission` flags |
|---------------------|--------------------------------------|
| `Read` | `FLAG_GRANT_READ_URI_PERMISSION` |
| `Write` | `FLAG_GRANT_WRITE_URI_PERMISSION` |
| `ReadWrite` | read + write flags |

---

## Step 3 — iOS picker host

Implement `StoragePickerHost` in **`shared/iosMain`** or **`iosApp`**. Present `UIDocumentPickerViewController` for a folder, then create a **security-scoped bookmark** and encode it as base64 for `StorageLocationToken.value`.

```kotlin
// Pseudocode — bookmark creation is typically done in Swift or a small UIKit bridge
val bookmarkData = url.bookmarkDataWithOptions(NSURLBookmarkCreationWithSecurityScope, ...)
val token = StorageLocationToken(Base64.encode(bookmarkData))
```

The module's `IosDocumentStorageProvider` resolves the bookmark and wraps I/O in `startAccessingSecurityScopedResource()`.

---

## Step 4 — Koin wiring

Register `storageFeatureModule` when storage is enabled. **`androidContext()` is required on Android** so `AndroidSafStorageProvider` can resolve `DocumentFile` URIs.

```kotlin
import com.devindie.myday.storage.api.StorageConfig
import com.devindie.myday.storage.api.storageFeatureModule

startKoinApp(
    appModules = listOf(
        storageFeatureModule(
            StorageConfig(
                enabled = true,
                pickerHost = androidStoragePickerHost, // or IosStoragePickerHost
            ),
        ),
    ),
) {
    androidContext(this@MyDayApplication)
}
```

When `enabled = false`, omit the module or pass `StorageConfig(enabled = false)` — all calls no-op via `NoOpStorageProvider`.

---

## Step 5 — Use in app code

```kotlin
class VaultSetupViewModel(
    private val storage: StorageClient,
) : ViewModel() {
    private var root: StorageLocationToken? = null

    fun pickVaultFolder() {
        viewModelScope.launch {
            when (
                val result =
                    storage.pickFolder(StoragePickRequest(StorageAccessMode.ReadWrite))
            ) {
                is StorageResult.Success -> {
                    root = result.value
                    // Persist result.value.value (e.g. DataStore) if needed across restarts
                }
                is StorageResult.Cancelled -> Unit
                is StorageResult.Failure -> { /* show result.error */ }
            }
        }
    }

    fun loadReadme() {
        val token = root ?: return
        viewModelScope.launch {
            when (val result = storage.readText(token, "README.md")) {
                is StorageResult.Success -> { /* use result.value */ }
                is StorageResult.Failure -> { /* PermissionDenied, NotFound, etc. */ }
                is StorageResult.Cancelled -> Unit
            }
        }
    }
}
```

---

## Step 6 — Verify

```bash
./gradlew :storage:allTests
./gradlew :storage:compileAndroidMain
./gradlew :storage:compileKotlinIosSimulatorArm64
./gradlew :architecture:test
```

**Expected:** `BUILD SUCCESSFUL` for each command.

---

## Setup checklist

- [ ] `:storage` included in `settings.gradle.kts`
- [ ] `implementation(projects.storage)` in consuming module + `:androidApp` (when using storage)
- [ ] `storageFeatureModule(StorageConfig(enabled = true, pickerHost = …))` in Koin
- [ ] `AndroidStoragePickerHost` with `OpenDocumentTree` + `takePersistableUriPermission`
- [ ] `IosStoragePickerHost` with security-scoped bookmark tokens
- [ ] `androidContext()` in `startKoinApp` (Android)
- [ ] **No** storage runtime permissions in manifest
- [ ] `./gradlew :storage:allTests` passes
- [ ] Consumer persists `StorageLocationToken` if access must survive process death

---

## Module layout

```
storage/
├── api/                          # Import from here
│   ├── StorageClient.kt
│   ├── StorageConfig.kt
│   ├── StorageModels.kt
│   ├── StorageFeatureModule.kt
│   └── provider/
│       ├── StorageProvider.kt
│       └── StoragePickerHost.kt
└── impl/                         # Internal — SAF, iOS scoped I/O, NoOp, Koin
    ├── StorageClientImpl.kt
    ├── StorageModule.kt
    ├── StoragePathValidator.kt
    ├── AndroidSafStorageProvider.kt      # androidMain
    ├── SafUriCodec.kt                    # androidMain
    ├── IosDocumentStorageProvider.kt     # iosMain
    └── SecurityScopedBookmarkCodec.kt    # iosMain
```

---

## Troubleshooting

| Symptom | Likely cause | Fix |
|---------|--------------|-----|
| `NotConfigured` | `enabled = false` or missing Koin module | Set `enabled = true`; register `storageFeatureModule` |
| `PermissionDenied` (Android) | Write without write flag at pick | Pick with `ReadWrite`; call `takePersistableUriPermission` with write flag |
| `PermissionDenied` (iOS) | Stale or non-scoped bookmark | Re-pick folder; ensure bookmark uses security scope |
| `NotFound` | Wrong `relativePath` or missing file | Check path segments; list parent first |
| `InvalidPath` | `..` or leading `/` in path | Use paths like `notes/readme.md` |
| Koin `Context` missing (Android) | No `androidContext()` | Pass `androidContext()` in `startKoinApp` |
| Accidental permission dialog | Legacy storage permission in manifest | Remove — not needed for SAF v1 |

---

## Further reading

- [Android SAF — OpenDocumentTree](https://developer.android.com/guide/topics/providers/document-provider)
- [iOS — security-scoped resource access](https://developer.apple.com/documentation/uikit/view_controllers/providing_access_to_directories)
- [KMP feature playbook — platform pickers](../docs/kmp-feature-playbook.md)
