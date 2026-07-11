package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.fake.FakeAiKeyRepository
import com.devindie.myday.domain.fake.FakeDailyNoteRepository
import com.devindie.myday.domain.fake.FakeDraftRepository
import com.devindie.myday.domain.fake.FakeReflectionPrefsRepository
import com.devindie.myday.domain.fake.FakeReflectionRepository
import com.devindie.myday.domain.model.reflection.DailyNoteContent
import com.devindie.myday.domain.model.reflection.DailyNoteRef
import com.devindie.myday.domain.model.reflection.DailyNoteResolution
import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GenerateReflectionDraftUseCaseTest {
    @Test
    fun insufficientContent_returnsInsufficient() = runTest {
        val notes =
            FakeDailyNoteRepository(
                content =
                DailyNoteContent(
                    ref = DailyNoteRef("2026-07-11", "2026-07-11.md", DailyNoteResolution.Fallback),
                    body = "# Title\n",
                    contentHash = "h1",
                ),
            )
        val uc = createUseCase(notes = notes)

        val result = uc()

        assertTrue(result.isFailure)
        assertIs<ReflectionError.InsufficientContent>(result.exceptionOrNull())
    }

    @Test
    fun noConsent_returnsConsentRequired() = runTest {
        val uc =
            createUseCase(
                prefs =
                FakeReflectionPrefsRepository(
                    ReflectionPrefs(consentAccepted = false, featureEnabled = true),
                ),
            )

        val result = uc()

        assertTrue(result.isFailure)
        assertIs<ReflectionError.ConsentRequired>(result.exceptionOrNull())
    }

    @Test
    fun noKey_returnsKeyMissing() = runTest {
        val uc = createUseCase(keys = FakeAiKeyRepository(key = null))

        val result = uc()

        assertTrue(result.isFailure)
        assertIs<ReflectionError.KeyMissing>(result.exceptionOrNull())
    }

    @Test
    fun missingNote_returnsDailyNoteMissing() = runTest {
        val uc =
            createUseCase(
                notes = FakeDailyNoteRepository(content = null),
            )

        val result = uc()

        assertTrue(result.isFailure)
        assertIs<ReflectionError.DailyNoteMissing>(result.exceptionOrNull())
    }

    @Test
    fun success_storesDraft() = runTest {
        val drafts = FakeDraftRepository()
        val reflections = FakeReflectionRepository(generateResult = Result.success("# Daily Reflection\n\nDone."))
        val body = sufficientBody()
        val notes =
            FakeDailyNoteRepository(
                content =
                DailyNoteContent(
                    ref = DailyNoteRef("2026-07-11", "2026-07-11.md", DailyNoteResolution.Fallback),
                    body = body,
                    contentHash = "hash-1",
                ),
            )
        val uc =
            createUseCase(
                notes = notes,
                drafts = drafts,
                reflections = reflections,
                clock = { 1_234L },
            )

        val result = uc()

        assertTrue(result.isSuccess)
        val draft = result.getOrThrow()
        assertEquals("2026-07-11", draft.date)
        assertEquals("# Daily Reflection\n\nDone.", draft.markdown)
        assertEquals("hash-1", draft.sourceContentHash)
        assertEquals(1_234L, draft.generatedAtEpochMs)
        assertEquals(draft, drafts.get("2026-07-11"))
        assertNotNull(reflections.lastGenerateArgs)
        assertEquals(body, reflections.lastGenerateArgs!!.first)
    }

    private fun createUseCase(
        notes: FakeDailyNoteRepository = FakeDailyNoteRepository(content = defaultNote()),
        drafts: FakeDraftRepository = FakeDraftRepository(),
        keys: FakeAiKeyRepository = FakeAiKeyRepository(key = "sk-test"),
        prefs: FakeReflectionPrefsRepository =
            FakeReflectionPrefsRepository(
                ReflectionPrefs(consentAccepted = true, featureEnabled = true),
            ),
        reflections: FakeReflectionRepository = FakeReflectionRepository(),
        clock: () -> Long = { 0L },
    ): GenerateReflectionDraftUseCase = GenerateReflectionDraftUseCase(
        dailyNotes = notes,
        drafts = drafts,
        keys = keys,
        prefs = prefs,
        reflections = reflections,
        clock = clock,
        todayIso = { "2026-07-11" },
    )

    private fun defaultNote(): DailyNoteContent = DailyNoteContent(
        ref = DailyNoteRef("2026-07-11", "2026-07-11.md", DailyNoteResolution.Fallback),
        body = sufficientBody(),
        contentHash = "hash-default",
    )

    private fun sufficientBody(): String = "Today I worked on reflection use cases. ".repeat(12)
}
