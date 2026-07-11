# Daily Reflection Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ship Daily Reflection MVP — link an Obsidian Vault, resolve today’s Daily Note, Auto-Draft / manually generate a Balanced Reflection via OpenRouter BYOK, review an in-app Draft, and Save a Reflection file plus Embed Link.

**Architecture:** Domain owns Daily Note / Draft / Reflection contracts and use cases. Data implements Obsidian path resolution and vault I/O via `:storage`, OpenRouter HTTP, KSafe key storage, and local Draft/prefs. Shared `feature/dailyreflection` provides Reflect tab UI. Platform scheduler (WorkManager / iOS BG) calls the same generate-draft use case. Follow [docs/kmp-feature-playbook.md](../../kmp-feature-playbook.md).

**Tech Stack:** Kotlin Multiplatform, Koin, Coroutines/Flow, DataStore, KSafe, Ktor (OpenRouter), `:storage`, WorkManager (Android), Compose Multiplatform, kotlin-test + Turbine, Konsist.

**Spec:** [`docs/superpowers/specs/2026-07-11-daily-reflection-design.md`](../specs/2026-07-11-daily-reflection-design.md)  
**Glossary:** [`CONTEXT.md`](../../../CONTEXT.md)

---

## File map

| File | Responsibility |
|------|----------------|
| `domain/.../model/reflection/ReflectionModels.kt` | `LocalDate` string helpers, `DailyNoteRef`, `Draft`, `ReflectionDocument`, prefs, errors |
| `domain/.../reflection/ContentSufficiency.kt` | Sufficient Content heuristic |
| `domain/.../reflection/SourceTruncation.kt` | End-cap truncation + flag |
| `domain/.../reflection/MomentDatePathFormatter.kt` | Moment-token subset → relative path |
| `domain/.../reflection/EmbedLink.kt` | Build/detect `![[...]]` embed |
| `domain/.../repository/DailyNoteRepository.kt` | Resolve/read Daily Note; vault link |
| `domain/.../repository/DraftRepository.kt` | Per-date Draft CRUD |
| `domain/.../repository/ReflectionRepository.kt` | Generate + save to vault |
| `domain/.../repository/AiKeyRepository.kt` | OpenRouter key |
| `domain/.../repository/ReflectionPrefsRepository.kt` | Consent, window, folder, model, enabled |
| `domain/.../repository/ReflectionSchedulerPort.kt` | Schedule/cancel Auto-Draft (bound at app) |
| `domain/.../usecase/reflection/*.kt` | Use cases |
| `domain/.../fake/Fake*Reflection*.kt` | Test fakes |
| `data/.../reflection/**` | Resolver, vault DS, OpenRouter, DraftStore, prefs, repos, Koin |
| `data/build.gradle.kts` | `implementation(projects.storage)` |
| `shared/.../feature/dailyreflection/**` | Feature module, routes, VM, screens |
| `shared/.../core/navigation/MainRoute.kt` | Add `Reflect` (replace `Collection` tab) |
| `shared/.../feature/main/api/MainDestination.kt` | Reflect tab |
| `shared/.../core/di/AppDomainModule.kt` | Register use cases + feature module |
| `androidApp/...` | Storage picker host, WorkManager scheduler, Koin |
| `shared/.../KoinIos.kt` + iOS picker/scheduler | iOS wiring |
| `architecture/.../LayerDependencyTest.kt` | Allow `data`→`:storage`; forbid `domain`/`shared`→`:storage` |

---

### Task 1: Domain pure helpers — sufficiency, truncation, embed, Moment path

**Files:**
- Create: `domain/src/commonMain/kotlin/com/devindie/myday/domain/reflection/ContentSufficiency.kt`
- Create: `domain/src/commonMain/kotlin/com/devindie/myday/domain/reflection/SourceTruncation.kt`
- Create: `domain/src/commonMain/kotlin/com/devindie/myday/domain/reflection/EmbedLink.kt`
- Create: `domain/src/commonMain/kotlin/com/devindie/myday/domain/reflection/MomentDatePathFormatter.kt`
- Create: `domain/src/commonMain/kotlin/com/devindie/myday/domain/model/reflection/ReflectionConstants.kt`
- Test: `domain/src/commonTest/kotlin/com/devindie/myday/domain/reflection/ContentSufficiencyTest.kt`
- Test: `domain/src/commonTest/kotlin/com/devindie/myday/domain/reflection/MomentDatePathFormatterTest.kt`
- Test: `domain/src/commonTest/kotlin/com/devindie/myday/domain/reflection/EmbedLinkTest.kt`
- Test: `domain/src/commonTest/kotlin/com/devindie/myday/domain/reflection/SourceTruncationTest.kt`

- [ ] **Step 1: Write failing tests**

```kotlin
// ContentSufficiencyTest.kt
package com.devindie.myday.domain.reflection

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContentSufficiencyTest {
    @Test
    fun empty_isInsufficient() {
        assertFalse(ContentSufficiency.isSufficient(""))
    }

    @Test
    fun headingsAndEmptyTasksOnly_isInsufficient() {
        val text = """
            # 2026-07-11
            ## Tasks
            - [ ]
            - [ ]
        """.trimIndent()
        assertFalse(ContentSufficiency.isSufficient(text))
    }

    @Test
    fun longProse_isSufficient() {
        assertTrue(ContentSufficiency.isSufficient("x".repeat(200)))
    }
}
```

```kotlin
// MomentDatePathFormatterTest.kt
package com.devindie.myday.domain.reflection

import kotlin.test.Test
import kotlin.test.assertEquals

class MomentDatePathFormatterTest {
    @Test
    fun formatsNestedPath() {
        val path = MomentDatePathFormatter.format(
            folder = "Journal",
            format = "YYYY/MM/YYYY-MM-DD",
            year = 2026,
            month = 7,
            day = 11,
        )
        assertEquals("Journal/2026/07/2026-07-11.md", path)
    }

    @Test
    fun unsupportedToken_returnsNull() {
        val path = MomentDatePathFormatter.format(
            folder = "",
            format = "dddd",
            year = 2026,
            month = 7,
            day = 11,
        )
        assertEquals(null, path)
    }
}
```

```kotlin
// EmbedLinkTest.kt — assert buildEmbed("reflections", "2026-07-11") == "![[reflections/2026-07-11]]"
// and containsEmbed detects existing link; SourceTruncationTest — end slice + truncated=true
```

- [ ] **Step 2: Run tests — expect FAIL**

```bash
./gradlew :domain:cleanAllTests :domain:allTests --tests "com.devindie.myday.domain.reflection.*"
```

Expected: FAIL (types missing).

- [ ] **Step 3: Implement helpers**

```kotlin
// ReflectionConstants.kt
package com.devindie.myday.domain.model.reflection

object ReflectionConstants {
    const val MIN_SUFFICIENT_CHARS = 200
    const val MAX_SOURCE_CHARS = 24_000
    const val DEFAULT_REFLECTION_FOLDER = "reflections"
    const val DEFAULT_OPENROUTER_MODEL = "openai/gpt-4o-mini"
    const val DEFAULT_WINDOW_START_MINUTE = 20 * 60 // 20:00
    const val DEFAULT_WINDOW_END_MINUTE = 22 * 60 // 22:00
}
```

```kotlin
// ContentSufficiency.kt
package com.devindie.myday.domain.reflection

import com.devindie.myday.domain.model.reflection.ReflectionConstants

object ContentSufficiency {
    private val headingOrEmptyTask =
        Regex("""^\s*(#{1,6}\s+.*|- \[[ xX]?\]\s*)$""")

    fun isSufficient(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.length < ReflectionConstants.MIN_SUFFICIENT_CHARS) return false
        val substantive =
            trimmed.lineSequence()
                .map { it.trimEnd() }
                .filter { it.isNotBlank() }
                .filterNot { headingOrEmptyTask.matches(it) }
                .joinToString("\n")
        return substantive.length >= ReflectionConstants.MIN_SUFFICIENT_CHARS
    }
}
```

```kotlin
// SourceTruncation.kt
package com.devindie.myday.domain.reflection

import com.devindie.myday.domain.model.reflection.ReflectionConstants

data class TruncatedSource(val text: String, val truncated: Boolean)

object SourceTruncation {
    fun fromEnd(
        text: String,
        maxChars: Int = ReflectionConstants.MAX_SOURCE_CHARS,
    ): TruncatedSource {
        if (text.length <= maxChars) return TruncatedSource(text, truncated = false)
        return TruncatedSource(text.takeLast(maxChars), truncated = true)
    }
}
```

```kotlin
// EmbedLink.kt
package com.devindie.myday.domain.reflection

object EmbedLink {
    fun wikiPath(folder: String, dateIso: String): String =
        "${folder.trim('/').trim()}/$dateIso"

    fun build(folder: String, dateIso: String): String =
        "![[${wikiPath(folder, dateIso)}]]"

    fun contains(dailyNoteBody: String, folder: String, dateIso: String): Boolean {
        val target = wikiPath(folder, dateIso)
        return dailyNoteBody.contains("![[$target]]") ||
            dailyNoteBody.contains("![[$target.md]]")
    }

    fun appendBlock(dailyNoteBody: String, folder: String, dateIso: String): String {
        if (contains(dailyNoteBody, folder, dateIso)) return dailyNoteBody
        val block = "\n\n## Daily Reflection\n\n${build(folder, dateIso)}\n"
        return dailyNoteBody.trimEnd() + block
    }
}
```

```kotlin
// MomentDatePathFormatter.kt
package com.devindie.myday.domain.reflection

object MomentDatePathFormatter {
    private val supported = setOf("YYYY", "MM", "DD", "HH", "mm")

    /**
     * @return relative path including `.md`, or null if [format] has unsupported tokens.
     */
    fun format(
        folder: String,
        format: String,
        year: Int,
        month: Int,
        day: Int,
        hour: Int = 0,
        minute: Int = 0,
    ): String? {
        if (format.contains("dddd", ignoreCase = false) ||
            format.contains("MMMM") ||
            format.contains("ddd")
        ) {
            return null
        }
        var result = format
        val replacements =
            listOf(
                "YYYY" to year.toString().padStart(4, '0'),
                "MM" to month.toString().padStart(2, '0'),
                "DD" to day.toString().padStart(2, '0'),
                "HH" to hour.toString().padStart(2, '0'),
                "mm" to minute.toString().padStart(2, '0'),
            )
        // Replace longest tokens first (already ordered)
        for ((token, value) in replacements) {
            result = result.replace(token, value)
        }
        // Reject leftover letter runs that look like moment tokens
        if (Regex("""[YyMdHhmsaA]{2,}""").containsMatchIn(result)) return null
        val file = if (result.endsWith(".md")) result else "$result.md"
        val prefix = folder.trim('/').trim()
        return if (prefix.isEmpty()) file else "$prefix/$file"
    }
}
```

- [ ] **Step 4: Run tests — expect PASS**

```bash
./gradlew :domain:allTests --tests "com.devindie.myday.domain.reflection.*"
```

- [ ] **Step 5: Commit**

```bash
git add domain/src/commonMain/kotlin/com/devindie/myday/domain/reflection \
  domain/src/commonMain/kotlin/com/devindie/myday/domain/model/reflection \
  domain/src/commonTest/kotlin/com/devindie/myday/domain/reflection
git commit -m "feat(reflection): add sufficiency, truncation, embed, and Moment path helpers"
```

---

### Task 2: Domain models + repository interfaces

**Files:**
- Create: `domain/src/commonMain/kotlin/com/devindie/myday/domain/model/reflection/ReflectionModels.kt`
- Create: repository interfaces listed in file map
- Test: `domain/src/commonTest/kotlin/com/devindie/myday/domain/model/reflection/ReflectionModelsTest.kt` (smoke)

- [ ] **Step 1: Define models**

```kotlin
package com.devindie.myday.domain.model.reflection

/** Calendar date as ISO `YYYY-MM-DD` (domain stays free of kotlinx-datetime if not already used). */
typealias IsoDate = String

data class VaultLink(
    val tokenValue: String,
)

data class DailyNoteRef(
    val date: IsoDate,
    val relativePath: String,
    val resolution: DailyNoteResolution,
)

enum class DailyNoteResolution {
    PeriodicNotes,
    CoreDailyNotes,
    Fallback,
}

data class DailyNoteContent(
    val ref: DailyNoteRef,
    val body: String,
    val contentHash: String,
)

data class Draft(
    val date: IsoDate,
    val markdown: String,
    val sourceContentHash: String,
    val sourceTruncated: Boolean,
    val generatedAtEpochMs: Long,
)

data class ReflectionDocument(
    val date: IsoDate,
    val markdown: String,
    val relativePath: String,
)

data class ReflectionPrefs(
    val consentAccepted: Boolean = false,
    val featureEnabled: Boolean = false,
    val windowStartMinuteOfDay: Int = ReflectionConstants.DEFAULT_WINDOW_START_MINUTE,
    val windowEndMinuteOfDay: Int = ReflectionConstants.DEFAULT_WINDOW_END_MINUTE,
    val reflectionFolder: String = ReflectionConstants.DEFAULT_REFLECTION_FOLDER,
    val modelOverride: String? = null,
    val usedFallbackPathNoticeShown: Boolean = false,
)

sealed class ReflectionError : Exception() {
    data object VaultNotLinked : ReflectionError()
    data object VaultPermissionDenied : ReflectionError()
    data object ConsentRequired : ReflectionError()
    data object KeyMissing : ReflectionError()
    data object DailyNoteMissing : ReflectionError()
    data object InsufficientContent : ReflectionError()
    data object AlreadyExists : ReflectionError()
    data object Network : ReflectionError()
    data class Provider(val code: Int?, val message: String?) : ReflectionError()
    data object MalformedOutput : ReflectionError()
    data object Cancelled : ReflectionError()
    data object OutsideWindow : ReflectionError()
}
```

- [ ] **Step 2: Repository interfaces**

```kotlin
// DailyNoteRepository.kt
package com.devindie.myday.domain.repository

import com.devindie.myday.domain.model.reflection.DailyNoteContent
import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.model.reflection.VaultLink

interface DailyNoteRepository {
    suspend fun getVaultLink(): VaultLink?
    suspend fun setVaultLink(link: VaultLink)
    suspend fun clearVaultLink()
    /** @return null if file does not exist after resolution */
    suspend fun resolveAndRead(date: IsoDate): Result<DailyNoteContent?>
}
```

```kotlin
// DraftRepository.kt
interface DraftRepository {
    suspend fun get(date: IsoDate): Draft?
    suspend fun save(draft: Draft)
    suspend fun clear(date: IsoDate)
}

// AiKeyRepository.kt
interface AiKeyRepository {
    suspend fun getOpenRouterKey(): String?
    suspend fun setOpenRouterKey(key: String)
    suspend fun clearOpenRouterKey()
}

// ReflectionPrefsRepository.kt
interface ReflectionPrefsRepository {
    fun observe(): Flow<ReflectionPrefs>
    suspend fun get(): ReflectionPrefs
    suspend fun update(transform: (ReflectionPrefs) -> ReflectionPrefs)
}

// ReflectionRepository.kt
interface ReflectionRepository {
    suspend fun generateMarkdown(sourceText: String, model: String, apiKey: String): Result<String>
    suspend fun shortenMarkdown(currentMarkdown: String, model: String, apiKey: String): Result<String>
    suspend fun reflectionFileExists(date: IsoDate, folder: String): Result<Boolean>
    suspend fun saveToVault(
        date: IsoDate,
        folder: String,
        markdown: String,
        replaceExistingFile: Boolean,
    ): Result<ReflectionDocument>
}

// ReflectionSchedulerPort.kt
interface ReflectionSchedulerPort {
    fun reschedule(prefs: ReflectionPrefs)
    fun cancel()
}
```

- [ ] **Step 3: Commit**

```bash
git add domain/src/commonMain/kotlin/com/devindie/myday/domain/model/reflection \
  domain/src/commonMain/kotlin/com/devindie/myday/domain/repository
git commit -m "feat(reflection): add domain models and repository contracts"
```

---

### Task 3: Domain use cases + fakes + tests

**Files:**
- Create use cases under `domain/.../usecase/reflection/`
- Create fakes under `domain/.../fake/`
- Tests under `domain/.../commonTest/.../usecase/reflection/`

**Use cases to add:**

| Use case | Behavior |
|----------|----------|
| `LinkVaultUseCase` | Persist vault token from picker result (token string passed in) |
| `ObserveReflectionSetupUseCase` | Combine prefs + key-present + vault-linked |
| `UpdateReflectionPrefsUseCase` | Update prefs; call `ReflectionSchedulerPort.reschedule/cancel` |
| `SetOpenRouterKeyUseCase` / `ClearOpenRouterKeyUseCase` | KSafe via repo |
| `GetTodayDraftUseCase` | Load draft + optional stale flag vs current note hash |
| `GenerateReflectionDraftUseCase` | Gates → read note → sufficiency → truncate → OpenRouter → save Draft |
| `ShortenReflectionDraftUseCase` | Shorten current draft body |
| `SaveReflectionUseCase` | Confirm replace flag → write file + embed |
| `RunAutoDraftUseCase` | Same gates as generate + window + no existing successful draft |
| `SubmitReflectionFeedbackUseCase` | Validates reason enum only; returns `ReflectionFeedbackEvent` for UI to pass to `AnalyticsClient` (no repository; no journal text) |

- [ ] **Step 1: Write failing test for `GenerateReflectionDraftUseCase`**

```kotlin
class GenerateReflectionDraftUseCaseTest {
    @Test
    fun insufficientContent_returnsInsufficient() = runTest {
        val notes = FakeDailyNoteRepository(
            content = DailyNoteContent(
                ref = DailyNoteRef("2026-07-11", "2026-07-11.md", DailyNoteResolution.Fallback),
                body = "# Title\n",
                contentHash = "h1",
            ),
        )
        val uc = GenerateReflectionDraftUseCase(
            dailyNotes = notes,
            drafts = FakeDraftRepository(),
            keys = FakeAiKeyRepository(key = "sk-test"),
            prefs = FakeReflectionPrefsRepository(
                ReflectionPrefs(consentAccepted = true, featureEnabled = true),
            ),
            reflections = FakeReflectionRepository(),
            clock = { 0L },
            todayIso = { "2026-07-11" },
        )
        val result = uc()
        assertTrue(result.exceptionOrNull() is ReflectionError.InsufficientContent)
    }
}
```

Implement remaining gate tests: no consent, no key, missing note, success path stores Draft.

- [ ] **Step 2: Implement use cases minimally until tests pass**

`GenerateReflectionDraftUseCase` sketch:

```kotlin
class GenerateReflectionDraftUseCase(
    private val dailyNotes: DailyNoteRepository,
    private val drafts: DraftRepository,
    private val keys: AiKeyRepository,
    private val prefs: ReflectionPrefsRepository,
    private val reflections: ReflectionRepository,
    private val clock: () -> Long,
    private val todayIso: () -> IsoDate,
) {
    suspend operator fun invoke(): Result<Draft> = runCatching {
        val p = prefs.get()
        if (!p.consentAccepted) throw ReflectionError.ConsentRequired
        val key = keys.getOpenRouterKey() ?: throw ReflectionError.KeyMissing
        if (dailyNotes.getVaultLink() == null) throw ReflectionError.VaultNotLinked
        val note = dailyNotes.resolveAndRead(todayIso()).getOrThrow()
            ?: throw ReflectionError.DailyNoteMissing
        if (!ContentSufficiency.isSufficient(note.body)) throw ReflectionError.InsufficientContent
        val truncated = SourceTruncation.fromEnd(note.body)
        val model = p.modelOverride?.takeIf { it.isNotBlank() }
            ?: ReflectionConstants.DEFAULT_OPENROUTER_MODEL
        val markdown = reflections.generateMarkdown(truncated.text, model, key).getOrThrow()
        val draft = Draft(
            date = note.ref.date,
            markdown = markdown,
            sourceContentHash = note.contentHash,
            sourceTruncated = truncated.truncated,
            generatedAtEpochMs = clock(),
        )
        drafts.save(draft)
        draft
    }
}
```

`RunAutoDraftUseCase`: if draft already exists for today → success no-op; if outside window → skip (return success without work or a dedicated `Skipped`); else call generate.

Window check:

```kotlin
fun isWithinWindow(minuteOfDay: Int, start: Int, end: Int): Boolean =
    if (start <= end) minuteOfDay in start until end
    else minuteOfDay >= start || minuteOfDay < end // overnight windows
```

- [ ] **Step 3: Run domain tests**

```bash
./gradlew :domain:allTests
```

Expected: PASS.

- [ ] **Step 4: Commit**

```bash
git commit -m "feat(reflection): add reflection use cases and domain tests"
```

---

### Task 4: Wire `:storage` into `:data` + architecture rule update

**Files:**
- Modify: `data/build.gradle.kts` — `implementation(projects.storage)`
- Modify: `architecture/src/test/kotlin/com/devindie/myday/architecture/layer/LayerDependencyTest.kt` (and any storage-specific test from storage plan)
- Create: `androidApp/.../storage/AndroidStoragePickerHost.kt` if missing
- Wire: `storageFeatureModule` in Android `Application` / iOS `doInitKoin`

- [ ] **Step 1: Add Gradle dependency**

In `data/build.gradle.kts` `commonMain.dependencies`:

```kotlin
implementation(projects.storage)
```

- [ ] **Step 2: Update Konsist**

Assert:

- `domain` must not import `com.devindie.myday.storage`
- `shared` production code must not import `com.devindie.myday.storage` (picker hosts may live in `androidApp` / `iosMain` app wiring)
- `data` **may** import `com.devindie.myday.storage`

Remove any rule that forbids `data` → storage if present from the storage module plan.

- [ ] **Step 3: Verify**

```bash
./gradlew :data:compileKotlinIosSimulatorArm64 :architecture:test
```

(or Android compile equivalent)

- [ ] **Step 4: Commit**

```bash
git commit -m "build: allow data layer to use storage module for vault I/O"
```

---

### Task 5: Data — prefs, vault token, AI key, draft store

**Files:**
- Create: `data/.../reflection/ReflectionPrefsDataStore.kt` + `ReflectionPrefsRepositoryImpl.kt`
- Create: `data/.../reflection/VaultLinkStore.kt` (DataStore string for token)
- Create: `data/.../reflection/DraftLocalDataSource.kt` + `DraftRepositoryImpl.kt`
- Create: `data/.../reflection/AiKeyRepositoryImpl.kt` (KSafe key, separate from auth tokens)
- Tests with fakes / in-memory DataStore patterns used by `SettingsRepositoryImplTest`

- [ ] **Step 1: Failing tests for prefs round-trip and draft save/get**
- [ ] **Step 2: Implement with `DispatcherProvider.io` via existing `runIo` helpers**
- [ ] **Step 3: KSafe key under a dedicated key name e.g. `openrouter_api_key`**
- [ ] **Step 4: Commit**

```bash
git commit -m "feat(reflection): persist prefs, vault token, drafts, and OpenRouter key"
```

---

### Task 6: Data — Obsidian resolver + DailyNoteRepositoryImpl

**Files:**
- Create: `data/.../reflection/ObsidianDailyNotesConfig.kt` (DTO parse)
- Create: `data/.../reflection/PeriodicNotesConfig.kt`
- Create: `data/.../reflection/ObsidianDailyNoteResolver.kt`
- Create: `data/.../reflection/VaultNoteDataSource.kt` (wraps `StorageClient`)
- Create: `data/.../reflection/DailyNoteRepositoryImpl.kt`
- Test: resolver unit tests with fixture JSON strings (no real SAF)

- [ ] **Step 1: Failing resolver tests**

```kotlin
@Test
fun prefersPeriodicNotesOverCore() {
    val storage = FakeVaultFiles(
        mapOf(
            ".obsidian/plugins/periodic-notes/data.json" to """
                {"daily":{"format":"YYYY-MM-DD","folder":"periodic","enabled":true}}
            """.trimIndent(),
            ".obsidian/daily-notes.json" to """{"format":"YYYY-MM-DD","folder":"Daily"}""",
            "periodic/2026-07-11.md" to "hello from periodic",
        ),
    )
    val resolver = ObsidianDailyNoteResolver(storage)
    val ref = resolver.resolve("2026-07-11")
    assertEquals("periodic/2026-07-11.md", ref?.relativePath)
    assertEquals(DailyNoteResolution.PeriodicNotes, ref?.resolution)
}
```

Parse Periodic Notes `daily` object; if missing/disabled, read core `daily-notes.json`; else fallback `YYYY-MM-DD.md`.

Content hash: stable hash of body (e.g. SHA-256 hex or `body.hashCode()` is too weak — use a simple FNV/SHA via expect or `body.encodeToByteArray().contentHashCode()` combined with length for MVP, or kotlinx crypto if present). Prefer:

```kotlin
fun contentHash(body: String): String =
    body.encodeToByteArray().fold(0L) { acc, b -> (acc * 31) + (b.toLong() and 0xff) }.toString(16)
```

- [ ] **Step 2: Implement + pass tests**
- [ ] **Step 3: Commit**

```bash
git commit -m "feat(reflection): resolve Obsidian Daily Note paths and read vault files"
```

---

### Task 7: Data — OpenRouter client + ReflectionRepositoryImpl (generate + save)

**Files:**
- Create: `data/.../reflection/OpenRouterReflectionDataSource.kt`
- Create: `data/.../reflection/ReflectionPrompts.kt` (system + user prompt builders)
- Create: `data/.../reflection/ReflectionRepositoryImpl.kt`
- Test: `OpenRouterReflectionDataSourceTest` with `HttpClient` mock engine

- [ ] **Step 1: Failing HTTP test — Authorization header + model field**

```kotlin
@Test
fun sendsBearerAndModel() = runTest {
    val engine = MockEngine { request ->
        assertEquals("Bearer sk-test", request.headers["Authorization"])
        respond(
            content = """{"choices":[{"message":{"content":"## Today at a Glance\n\nHi"}}]}""",
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
    }
    val ds = OpenRouterReflectionDataSource(HttpClient(engine) {/* json */}, dispatchers)
    val text = ds.generate(source = "journal", model = "openai/gpt-4o-mini", apiKey = "sk-test")
    assertTrue(text.getOrThrow().contains("Today at a Glance"))
}
```

- [ ] **Step 2: Implement chat completions call**

POST `https://openrouter.ai/api/v1/chat/completions` with JSON body `model`, `messages` (system from PRD + user with source). Map 401 → `ReflectionError.Provider`, network → `Network`, empty content → `MalformedOutput`.

- [ ] **Step 3: `saveToVault`**

1. `relativePath = "$folder/$date.md"`
2. If exists && !replace → `Result.failure(ReflectionError.AlreadyExists)` (UI shows confirm)
3. `writeText` reflection file
4. Read Daily Note; `EmbedLink.appendBlock`; `writeText` daily note
5. Never log body

- [ ] **Step 4: Commit**

```bash
git commit -m "feat(reflection): OpenRouter generation and vault save with embed link"
```

---

### Task 8: Koin platform bindings for reflection

**Files:**
- Modify: `data/.../di/PlatformDataModule.android.kt` / `.ios.kt`
- Create: `data/.../reflection/ReflectionDataModule.kt` (common bindings) included from platform modules

Bind all `*Repository` impls, DataStores, OpenRouter client (can reuse app `HttpClient` or dedicated client without auth interceptor stealing user session — **use a separate HttpClient** without app bearer tokens).

- [ ] **Step 1: Register module; compile Android + iOS**
- [ ] **Step 2: Commit**

```bash
git commit -m "feat(reflection): wire reflection repositories in platform data modules"
```

---

### Task 9: Reflect tab navigation (replace Collection)

**Files:**
- Modify: `shared/.../core/navigation/MainRoute.kt` — replace `Collection` with `Reflect` **or** keep Collection route unused and add Reflect (prefer **replace Collection** to keep 4 tabs)
- Modify: `MainDestination.kt` — Reflect label + icon (`Icons.Filled.AutoAwesome` or `EditNote`)
- Modify: `MainNavigation.kt` / `mainTabNavGraph` — `reflectDestination()`
- Create: `shared/.../feature/dailyreflection/api/DailyReflectionFeatureModule.kt`
- Create: `shared/.../feature/dailyreflection/api/DailyReflectionNavigation.kt`
- Create: placeholder `DailyReflectionScreen.kt` (“Reflect setup coming”)
- Modify: `AppDomainModule.kt` — include feature module + `factoryOf` use cases

- [ ] **Step 1: Wire tab; app launches to Browse; Reflect tab shows placeholder**
- [ ] **Step 2: Update any `MainRoute.Collection` / `MainDestination.Collection` tests**
- [ ] **Step 3: Commit**

```bash
git commit -m "feat(reflection): add Reflect primary tab replacing Collection"
```

---

### Task 10: DailyReflectionViewModel + setup / draft UI

**Files:**
- Create: `shared/.../feature/dailyreflection/impl/DailyReflectionUiState.kt`
- Create: `shared/.../feature/dailyreflection/impl/DailyReflectionViewModel.kt`
- Create: screens: Setup, DraftReview, Generating
- Test: `shared/.../commonTest/.../DailyReflectionViewModelTest.kt` with fakes + `runViewModelTest`

**UiState (sealed / data):**

```kotlin
data class DailyReflectionUiState(
    val setup: SetupState,
    val draft: Draft?,
    val sourceTruncated: Boolean = false,
    val sourceChangedSinceDraft: Boolean = false,
    val isGenerating: Boolean = false,
    val error: ReflectionError? = null,
    val saveConfirmRequired: Boolean = false,
)

sealed interface SetupState {
    data object NeedsVault : SetupState
    data object NeedsConsent : SetupState
    data object NeedsKey : SetupState
    data class Ready(val prefs: ReflectionPrefs) : SetupState
}
```

**Intents:** pickVault (callback from platform), acceptConsent, saveKey, updateWindow, generate, cancel, editMarkdown, regenerate, shorten, requestSave, confirmSave, dismissError, submitFeedback.

- [x] **Step 1: ViewModel tests — consent gate, generate success, save confirm path**
- [x] **Step 2: Implement VM + Compose UI (calm layout per PRD)**
- [x] **Step 3: OpenRouter setup instructions screen (static markdown/text + link out)**
- [x] **Step 4: Commit**

```bash
git commit -m "feat(reflection): Reflect setup and draft review UI"
```

---

### Task 11: Platform Auto-Draft scheduler

**Files:**
- Create: `domain` already has `ReflectionSchedulerPort`
- Create: `androidApp` or `data/androidMain/.../AndroidReflectionScheduler.kt` using WorkManager
- Create: `iosMain/.../IosReflectionScheduler.kt` (BGTaskScheduler or best-effort foreground check on launch + notification — document iOS limits in KDoc)
- Bind port in app entry
- Call `RunAutoDraftUseCase` from worker
- Debug: `DEBUG`-only button “Run Auto-Draft now” on Reflect screen

**Android worker sketch:**

```kotlin
class AutoDraftWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val uc: RunAutoDraftUseCase = getKoin().get()
        return uc().fold(
            onSuccess = { Result.success() },
            onFailure = {
                if (it is ReflectionError.Network || it is ReflectionError.Provider) Result.retry()
                else Result.success() // don't retry consent/key/insufficient forever
            },
        )
    }
}
```

Schedule periodic work every 15–30 minutes; use case no-ops outside window / if draft exists.

- [x] **Step 1: Implement + manual verify with debug trigger**
- [x] **Step 2: Commit**

```bash
git commit -m "feat(reflection): schedule Auto-Draft within local time window"
```

---

### Task 12: Analytics + feedback feedback

**Files:**
- Create: `shared/.../dailyreflection` analytics event helpers using `:analytics` `AnalyticsClient`
- Wire Helpful / Not helpful chips on draft screen
- Ensure params exclude markdown bodies

Events (names from PRD, snake_case):  
`daily_reflection_opened`, `..._generation_started/completed/failed/cancelled`, `..._saved`, `..._helpful_selected`, `..._not_helpful_selected`, `..._insufficient_content`, `..._privacy_consent_accepted/declined`

- [x] **Step 1: Implement; unit-test that feedback params have no `markdown` / `source` keys**
- [x] **Step 2: Commit**

```bash
git commit -m "feat(reflection): add safe analytics and helpful feedback"
```

---

### Task 13: End-to-end quality gate

- [x] **Step 1: Run**

```bash
./gradlew :architecture:test :domain:allTests :data:allTests :shared:allTests
./gradlew qualityCheck
```

Expected: PASS (fix any Konsist/Spotless/Detekt issues).
Note: full `qualityCheck` may still fail linking `:shared` iOS simulator tests when Firebase/Xcode paths are missing on the machine; Android host tests + detekt + spotless + architecture are the gate used here.

- [ ] **Step 2: Manual smoke**

1. Pick Obsidian vault  
2. Consent + OpenRouter key  
3. Ensure today’s Daily Note has Sufficient Content  
4. Debug Auto-Draft or Generate  
5. Edit → Save → confirm `reflections/YYYY-MM-DD.md` + embed in Daily Note in Obsidian  
6. Disable feature → worker does not generate  

- [x] **Step 3: Final commit if fixes needed**

```bash
git commit -m "fix(reflection): address qualityCheck and smoke findings"
```

---

## Spec coverage checklist

| Spec requirement | Task(s) |
|------------------|---------|
| Vault via `:storage` | 4, 5, 6 |
| Periodic Notes → core → fallback | 6 |
| No create missing Daily Note | 3, 6 |
| BYOK OpenRouter + model override | 5, 7, 10 |
| Consent before generate | 3, 10 |
| Auto-Draft gates + window + retry | 3, 11 |
| Draft in-app only until Save | 3, 5, 7 |
| Stale Source Snapshot hint | 3, 10 |
| Reflection Folder + embed save | 1, 7 |
| Re-Save replace confirm | 7, 10 |
| End truncation + disclosure | 1, 3, 10 |
| Balanced prompt | 7 |
| Edit / regenerate / shorten / save | 3, 10 |
| Helpful feedback analytics | 12 |
| Reflect primary tab | 9 |
| Disable + delete key | 3, 5, 10, 11 |
| Architecture / quality | 4, 13 |

---

## Notes for implementers

- Do **not** put OpenRouter calls or `StorageClient` in ViewModels.
- Use a **dedicated** Ktor client for OpenRouter (no app session `Authorization` interceptor).
- Never log journal or reflection bodies (Kermit/crash).
- Keep Moment support limited to the documented subset; unsupported formats → fallback notice, not a wrong path.
- Prefer small commits per task; run the listed tests before each commit.
