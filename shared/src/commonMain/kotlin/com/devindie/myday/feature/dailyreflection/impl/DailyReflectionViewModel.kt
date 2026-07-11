package com.devindie.myday.feature.dailyreflection.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devindie.myday.analytics.api.AnalyticsClient
import com.devindie.myday.domain.model.reflection.Draft
import com.devindie.myday.domain.model.reflection.NotHelpfulReason
import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.domain.model.reflection.ReflectionSetupState
import com.devindie.myday.domain.repository.VaultPickerPort
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DailyReflectionViewModel(
    observeReflectionSetup: ObserveReflectionSetupUseCase,
    private val getReflectionSetup: GetReflectionSetupUseCase,
    private val getTodayDraft: GetTodayDraftUseCase,
    private val linkVault: LinkVaultUseCase,
    private val vaultPicker: VaultPickerPort,
    private val updateReflectionPrefs: UpdateReflectionPrefsUseCase,
    private val setOpenRouterKey: SetOpenRouterKeyUseCase,
    private val clearOpenRouterKey: ClearOpenRouterKeyUseCase,
    private val generateDraft: GenerateReflectionDraftUseCase,
    private val shortenDraft: ShortenReflectionDraftUseCase,
    private val saveReflection: SaveReflectionUseCase,
    private val runAutoDraft: RunAutoDraftUseCase,
    private val submitFeedback: SubmitReflectionFeedbackUseCase,
    private val analytics: AnalyticsClient,
    private val debugToolsEnabled: Boolean = false,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DailyReflectionUiState(debugToolsEnabled = debugToolsEnabled))
    val uiState: StateFlow<DailyReflectionUiState> = _uiState.asStateFlow()

    private var generateJob: Job? = null
    private var syncedDraftAtMs: Long? = null
    private var openedLogged = false

    init {
        viewModelScope.launch {
            observeReflectionSetup().collect {
                reloadState()
                if (!openedLogged) {
                    openedLogged = true
                    analytics.track(DailyReflectionAnalytics.opened())
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch { reloadState() }
    }

    fun pickVault() {
        viewModelScope.launch {
            vaultPicker
                .pickVault()
                .onSuccess { link -> linkVault(link.tokenValue) }
                .onFailure { error -> publishError(error) }
            reloadState()
        }
    }

    fun acceptConsent() {
        viewModelScope.launch {
            updateReflectionPrefs { prefs -> prefs.copy(consentAccepted = true) }
            analytics.track(DailyReflectionAnalytics.consentAccepted())
            reloadState()
        }
    }

    fun declineConsent() {
        analytics.track(DailyReflectionAnalytics.consentDeclined())
    }

    fun saveKey(key: String) {
        viewModelScope.launch {
            setOpenRouterKey(key.trim())
            reloadState()
        }
    }

    fun clearKey() {
        viewModelScope.launch {
            clearOpenRouterKey()
            reloadState()
        }
    }

    fun toggleEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateReflectionPrefs { prefs -> prefs.copy(featureEnabled = enabled) }
            reloadState()
        }
    }

    fun updateWindow(startMinuteOfDay: Int, endMinuteOfDay: Int) {
        viewModelScope.launch {
            updateReflectionPrefs { prefs ->
                prefs.copy(
                    windowStartMinuteOfDay = startMinuteOfDay,
                    windowEndMinuteOfDay = endMinuteOfDay,
                )
            }
            reloadState()
        }
    }

    fun updateFolder(folder: String) {
        viewModelScope.launch {
            updateReflectionPrefs { prefs -> prefs.copy(reflectionFolder = folder.trim()) }
            reloadState()
        }
    }

    fun updateModelOverride(model: String?) {
        viewModelScope.launch {
            updateReflectionPrefs { prefs ->
                prefs.copy(modelOverride = model?.trim()?.takeIf { it.isNotEmpty() })
            }
            reloadState()
        }
    }

    fun generate() {
        generateJob?.cancel()
        generateJob =
            viewModelScope.launch {
                _uiState.update { it.copy(isGenerating = true, errorMessage = null) }
                analytics.track(DailyReflectionAnalytics.generationStarted())
                generateDraft()
                    .onSuccess { draft ->
                        applyDraft(draft)
                        analytics.track(
                            DailyReflectionAnalytics.generationCompleted(draft.sourceTruncated),
                        )
                    }.onFailure { error ->
                        if (error is ReflectionError.InsufficientContent) {
                            analytics.track(DailyReflectionAnalytics.insufficientContent())
                        }
                        if (error !is CancellationException) {
                            analytics.track(DailyReflectionAnalytics.generationFailed(error))
                        }
                        publishError(error)
                    }
                _uiState.update { it.copy(isGenerating = false) }
                reloadState()
            }
    }

    fun cancelGenerate() {
        generateJob?.cancel()
        generateJob = null
        _uiState.update { it.copy(isGenerating = false) }
        analytics.track(DailyReflectionAnalytics.generationCancelled())
    }

    fun regenerate() {
        generate()
    }

    fun shorten() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, errorMessage = null) }
            shortenDraft()
                .onSuccess { draft -> applyDraft(draft) }
                .onFailure { error -> publishError(error) }
            _uiState.update { it.copy(isGenerating = false) }
            reloadState()
        }
    }

    fun editMarkdown(markdown: String) {
        _uiState.update { it.copy(editableMarkdown = markdown) }
    }

    fun requestSave() {
        viewModelScope.launch {
            val markdown = _uiState.value.editableMarkdown
            saveReflection(replaceExisting = false, markdownOverride = markdown)
                .onSuccess {
                    analytics.track(DailyReflectionAnalytics.saved(replaced = false))
                    _uiState.update { state ->
                        state.copy(
                            errorMessage = null,
                            saveConfirmRequired = false,
                        )
                    }
                }.onFailure { error ->
                    when (error) {
                        is ReflectionError.AlreadyExists ->
                            _uiState.update { it.copy(saveConfirmRequired = true) }
                        else -> publishError(error)
                    }
                }
        }
    }

    fun confirmSave(replace: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(saveConfirmRequired = false) }
            saveReflection(
                replaceExisting = replace,
                markdownOverride = _uiState.value.editableMarkdown,
            ).onSuccess {
                analytics.track(DailyReflectionAnalytics.saved(replaced = replace))
            }.onFailure { error -> publishError(error) }
        }
    }

    fun cancelSave() {
        _uiState.update { it.copy(saveConfirmRequired = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun submitHelpfulFeedback(helpful: Boolean, reason: NotHelpfulReason? = null) {
        val event = submitFeedback(helpful, reason)
        analytics.track(DailyReflectionAnalytics.feedback(event))
        _uiState.update { it.copy(feedbackSubmitted = true) }
    }

    fun runAutoDraftNow() {
        if (!debugToolsEnabled) return
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, errorMessage = null) }
            runCatching { runAutoDraft() }
                .onSuccess { reloadState() }
                .onFailure { error -> publishError(error) }
            _uiState.update { it.copy(isGenerating = false) }
        }
    }

    private suspend fun reloadState() {
        val setup = getReflectionSetup()
        val todayDraft = getTodayDraft()
        val draft = todayDraft.draft
        val editableMarkdown =
            if (draft != null && draft.generatedAtEpochMs != syncedDraftAtMs) {
                syncedDraftAtMs = draft.generatedAtEpochMs
                draft.markdown
            } else {
                _uiState.value.editableMarkdown
            }
        _uiState.update { current ->
            current.copy(
                setup = setup.toSetupState(),
                draft = draft,
                sourceTruncated = draft?.sourceTruncated ?: false,
                sourceChangedSinceDraft = todayDraft.sourceChanged,
                editableMarkdown = if (draft == null) "" else editableMarkdown,
                debugToolsEnabled = debugToolsEnabled,
            )
        }
    }

    private fun applyDraft(draft: Draft) {
        syncedDraftAtMs = draft.generatedAtEpochMs
        _uiState.update {
            it.copy(
                draft = draft,
                editableMarkdown = draft.markdown,
                sourceTruncated = draft.sourceTruncated,
                sourceChangedSinceDraft = false,
                errorMessage = null,
                feedbackSubmitted = false,
            )
        }
    }

    private fun publishError(error: Throwable) {
        if (error is CancellationException) return
        val message =
            when (error) {
                is ReflectionError -> error.toUserMessage()
                else -> error.message
            }
        if (message != null) {
            _uiState.update { it.copy(errorMessage = message) }
        }
    }

    private fun ReflectionSetupState.toSetupState(): SetupState {
        if (!vaultLinked) return SetupState.NeedsVault
        if (!prefs.consentAccepted) return SetupState.NeedsConsent
        if (!hasOpenRouterKey) return SetupState.NeedsKey
        return SetupState.Ready(prefs)
    }
}
