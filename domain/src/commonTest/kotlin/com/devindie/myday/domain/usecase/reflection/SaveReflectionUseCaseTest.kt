package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.fake.FakeDraftRepository
import com.devindie.myday.domain.fake.FakeReflectionPrefsRepository
import com.devindie.myday.domain.fake.FakeReflectionRepository
import com.devindie.myday.domain.model.reflection.Draft
import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SaveReflectionUseCaseTest {
    @Test
    fun noDraft_returnsDraftMissing() = runTest {
        val uc =
            SaveReflectionUseCase(
                drafts = FakeDraftRepository(),
                prefs = FakeReflectionPrefsRepository(ReflectionPrefs()),
                reflections = FakeReflectionRepository(),
                todayIso = { "2026-07-11" },
            )

        val result = uc(replaceExisting = false)

        assertTrue(result.isFailure)
        assertIs<ReflectionError.DraftMissing>(result.exceptionOrNull())
    }

    @Test
    fun replaceFalse_whenFileExists_returnsAlreadyExists() = runTest {
        val drafts = FakeDraftRepository()
        drafts.save(
            Draft(
                date = "2026-07-11",
                markdown = "# Reflection",
                sourceContentHash = "h1",
                sourceTruncated = false,
                generatedAtEpochMs = 0L,
            ),
        )
        val reflections = FakeReflectionRepository(existsResult = Result.success(true))
        val uc =
            SaveReflectionUseCase(
                drafts = drafts,
                prefs = FakeReflectionPrefsRepository(ReflectionPrefs()),
                reflections = reflections,
                todayIso = { "2026-07-11" },
            )

        val result = uc(replaceExisting = false)

        assertTrue(result.isFailure)
        assertIs<ReflectionError.AlreadyExists>(result.exceptionOrNull())
    }
}
