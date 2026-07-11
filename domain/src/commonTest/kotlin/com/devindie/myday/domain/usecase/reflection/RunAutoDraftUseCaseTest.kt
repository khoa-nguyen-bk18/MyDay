package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.fake.FakeAiKeyRepository
import com.devindie.myday.domain.fake.FakeDailyNoteRepository
import com.devindie.myday.domain.fake.FakeDraftRepository
import com.devindie.myday.domain.fake.FakeReflectionPrefsRepository
import com.devindie.myday.domain.fake.FakeReflectionRepository
import com.devindie.myday.domain.model.reflection.DailyNoteContent
import com.devindie.myday.domain.model.reflection.DailyNoteRef
import com.devindie.myday.domain.model.reflection.DailyNoteResolution
import com.devindie.myday.domain.model.reflection.Draft
import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RunAutoDraftUseCaseTest {
    @Test
    fun outsideWindow_returnsSkippedOutsideWindow() = runTest {
        val uc =
            createUseCase(
                minuteOfDay = { 12 * 60 },
                prefs =
                FakeReflectionPrefsRepository(
                    ReflectionPrefs(
                        consentAccepted = true,
                        featureEnabled = true,
                        windowStartMinuteOfDay = 20 * 60,
                        windowEndMinuteOfDay = 22 * 60,
                    ),
                ),
            )

        val result = uc()

        assertIs<AutoDraftResult.SkippedOutsideWindow>(result)
    }

    @Test
    fun draftExists_returnsSkippedDraftExists() = runTest {
        val drafts = FakeDraftRepository()
        drafts.save(
            Draft(
                date = "2026-07-11",
                markdown = "existing",
                sourceContentHash = "h1",
                sourceTruncated = false,
                generatedAtEpochMs = 0L,
            ),
        )
        val uc = createUseCase(drafts = drafts, minuteOfDay = { 21 * 60 })

        val result = uc()

        assertIs<AutoDraftResult.SkippedDraftExists>(result)
    }

    @Test
    fun success_returnsGeneratedDraft() = runTest {
        val uc = createUseCase(minuteOfDay = { 21 * 60 })

        val result = uc()

        val generated = assertIs<AutoDraftResult.Generated>(result)
        assertEquals("2026-07-11", generated.draft.date)
    }

    private fun createUseCase(
        drafts: FakeDraftRepository = FakeDraftRepository(),
        minuteOfDay: () -> Int = { 21 * 60 },
        prefs: FakeReflectionPrefsRepository =
            FakeReflectionPrefsRepository(
                ReflectionPrefs(consentAccepted = true, featureEnabled = true),
            ),
    ): RunAutoDraftUseCase {
        val notes =
            FakeDailyNoteRepository(
                content =
                DailyNoteContent(
                    ref = DailyNoteRef("2026-07-11", "2026-07-11.md", DailyNoteResolution.Fallback),
                    body = "Today I reflected on meaningful progress across several areas. ".repeat(12),
                    contentHash = "hash-1",
                ),
            )
        val generate =
            GenerateReflectionDraftUseCase(
                dailyNotes = notes,
                drafts = drafts,
                keys = FakeAiKeyRepository(key = "sk-test"),
                prefs = prefs,
                reflections = FakeReflectionRepository(),
                clock = { 99L },
                todayIso = { "2026-07-11" },
            )
        return RunAutoDraftUseCase(
            drafts = drafts,
            prefs = prefs,
            generate = generate,
            minuteOfDay = minuteOfDay,
            todayIso = { "2026-07-11" },
        )
    }
}
