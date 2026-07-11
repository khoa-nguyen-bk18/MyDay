package com.devindie.myday.feature.dailyreflection.impl

import com.devindie.myday.domain.model.reflection.DailyNoteContent
import com.devindie.myday.domain.model.reflection.DailyNoteRef
import com.devindie.myday.domain.model.reflection.DailyNoteResolution
import com.devindie.myday.domain.model.reflection.NotHelpfulReason
import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import com.devindie.myday.domain.usecase.reflection.ClearOpenRouterKeyUseCase
import com.devindie.myday.domain.usecase.reflection.GenerateReflectionDraftUseCase
import com.devindie.myday.domain.usecase.reflection.GetReflectionSetupUseCase
import com.devindie.myday.domain.usecase.reflection.GetTodayDraftUseCase
import com.devindie.myday.domain.usecase.reflection.LinkVaultUseCase
import com.devindie.myday.domain.usecase.reflection.ObserveReflectionSetupUseCase
import com.devindie.myday.domain.usecase.reflection.RunAutoDraftUseCase
import com.devindie.myday.domain.usecase.reflection.SaveReflectionUseCase
import com.devindie.myday.domain.usecase.reflection.SetOpenRouterKeyUseCase
import com.devindie.myday.domain.usecase.reflection.ShortenReflectionDraftUseCase
import com.devindie.myday.domain.usecase.reflection.SubmitReflectionFeedbackUseCase
import com.devindie.myday.domain.usecase.reflection.UpdateReflectionPrefsUseCase
import com.devindie.myday.fake.FakeAiKeyRepository
import com.devindie.myday.fake.FakeAnalyticsClient
import com.devindie.myday.fake.FakeDailyNoteRepository
import com.devindie.myday.fake.FakeDraftRepository
import com.devindie.myday.fake.FakeReflectionPrefsRepository
import com.devindie.myday.fake.FakeReflectionRepository
import com.devindie.myday.fake.FakeReflectionSchedulerPort
import com.devindie.myday.fake.FakeVaultPickerPort
import com.devindie.myday.test.advanceMainUntilIdle
import com.devindie.myday.test.runViewModelTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DailyReflectionViewModelTest {
    @Test
    fun consentGate_acceptConsent_thenNeedsKey() = runViewModelTest {
        val prefs =
            FakeReflectionPrefsRepository(
                ReflectionPrefs(consentAccepted = false, featureEnabled = true),
            )
        val viewModel = createViewModel(prefs = prefs, keys = FakeAiKeyRepository(key = null))

        advanceMainUntilIdle()

        assertIs<SetupState.NeedsConsent>(viewModel.uiState.value.setup)

        viewModel.acceptConsent()
        advanceMainUntilIdle()

        assertIs<SetupState.NeedsKey>(viewModel.uiState.value.setup)

        viewModel.saveKey("sk-test")
        advanceMainUntilIdle()

        val ready = viewModel.uiState.value.setup
        assertIs<SetupState.Ready>(ready)
        assertEquals(true, ready.prefs.consentAccepted)
    }

    @Test
    fun generate_success_showsDraft() = runViewModelTest {
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
        val reflections =
            FakeReflectionRepository(
                generateResult = Result.success("# Daily Reflection\n\nGenerated."),
            )
        val viewModel =
            createViewModel(
                notes = notes,
                reflections = reflections,
                prefs =
                FakeReflectionPrefsRepository(
                    ReflectionPrefs(consentAccepted = true, featureEnabled = true),
                ),
            )

        advanceMainUntilIdle()
        assertIs<SetupState.Ready>(viewModel.uiState.value.setup)
        assertNull(viewModel.uiState.value.draft)

        viewModel.generate()
        advanceMainUntilIdle()

        assertEquals(false, viewModel.uiState.value.isGenerating)
        val draft = viewModel.uiState.value.draft
        assertNotNull(draft)
        assertEquals("# Daily Reflection\n\nGenerated.", draft.markdown)
        assertEquals("# Daily Reflection\n\nGenerated.", viewModel.uiState.value.editableMarkdown)
    }

    @Test
    fun feedbackParams_excludeMarkdownAndSource() = runViewModelTest {
        val analytics = FakeAnalyticsClient()
        val viewModel = createViewModel(analytics = analytics)

        advanceMainUntilIdle()
        viewModel.submitHelpfulFeedback(helpful = false, reason = NotHelpfulReason.TooGeneric)
        advanceMainUntilIdle()

        val event =
            analytics.events.last {
                it.name == DailyReflectionAnalyticsEvents.NOT_HELPFUL_SELECTED
            }
        assertEquals("TooGeneric", event.params["reason"])
        assertFalse(event.params.containsKey("markdown"))
        assertFalse(event.params.containsKey("source"))
        assertTrue(viewModel.uiState.value.feedbackSubmitted)
    }

    private fun createViewModel(
        prefs: FakeReflectionPrefsRepository =
            FakeReflectionPrefsRepository(
                ReflectionPrefs(consentAccepted = true, featureEnabled = true),
            ),
        keys: FakeAiKeyRepository = FakeAiKeyRepository(key = "sk-test"),
        notes: FakeDailyNoteRepository = FakeDailyNoteRepository(content = defaultNote()),
        drafts: FakeDraftRepository = FakeDraftRepository(),
        reflections: FakeReflectionRepository = FakeReflectionRepository(),
        vaultPicker: FakeVaultPickerPort = FakeVaultPickerPort(),
        analytics: FakeAnalyticsClient = FakeAnalyticsClient(),
    ): DailyReflectionViewModel {
        val scheduler = FakeReflectionSchedulerPort()
        val generate =
            GenerateReflectionDraftUseCase(
                dailyNotes = notes,
                drafts = drafts,
                keys = keys,
                prefs = prefs,
                reflections = reflections,
                clock = { 1_234L },
                todayIso = { "2026-07-11" },
            )
        return DailyReflectionViewModel(
            observeReflectionSetup = ObserveReflectionSetupUseCase(prefs, keys, notes),
            getReflectionSetup = GetReflectionSetupUseCase(prefs, keys, notes),
            getTodayDraft = GetTodayDraftUseCase(drafts, notes) { "2026-07-11" },
            linkVault = LinkVaultUseCase(notes),
            vaultPicker = vaultPicker,
            updateReflectionPrefs = UpdateReflectionPrefsUseCase(prefs, scheduler),
            setOpenRouterKey = SetOpenRouterKeyUseCase(keys),
            clearOpenRouterKey = ClearOpenRouterKeyUseCase(keys),
            generateDraft = generate,
            shortenDraft =
            ShortenReflectionDraftUseCase(
                drafts = drafts,
                keys = keys,
                prefs = prefs,
                reflections = reflections,
                todayIso = { "2026-07-11" },
            ),
            saveReflection =
            SaveReflectionUseCase(
                drafts = drafts,
                prefs = prefs,
                reflections = reflections,
                todayIso = { "2026-07-11" },
            ),
            runAutoDraft =
            RunAutoDraftUseCase(
                drafts = drafts,
                prefs = prefs,
                generate = generate,
                minuteOfDay = { 21 * 60 },
                todayIso = { "2026-07-11" },
            ),
            submitFeedback = SubmitReflectionFeedbackUseCase(),
            analytics = analytics,
            debugToolsEnabled = true,
        )
    }

    private fun defaultNote(): DailyNoteContent = DailyNoteContent(
        ref = DailyNoteRef("2026-07-11", "2026-07-11.md", DailyNoteResolution.Fallback),
        body = sufficientBody(),
        contentHash = "hash-default",
    )

    private fun sufficientBody(): String = "Today I worked on reflection use cases. ".repeat(12)
}
