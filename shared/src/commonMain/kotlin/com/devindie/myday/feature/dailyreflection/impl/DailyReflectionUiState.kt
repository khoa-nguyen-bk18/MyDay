package com.devindie.myday.feature.dailyreflection.impl

import com.devindie.myday.domain.model.reflection.Draft
import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.domain.model.reflection.ReflectionPrefs

data class DailyReflectionUiState(
    val setup: SetupState = SetupState.Loading,
    val draft: Draft? = null,
    val sourceTruncated: Boolean = false,
    val sourceChangedSinceDraft: Boolean = false,
    val isGenerating: Boolean = false,
    val errorMessage: String? = null,
    val saveConfirmRequired: Boolean = false,
    val editableMarkdown: String = "",
    val feedbackSubmitted: Boolean = false,
    val debugToolsEnabled: Boolean = false,
)

sealed interface SetupState {
    data object Loading : SetupState

    data object NeedsVault : SetupState

    data object NeedsConsent : SetupState

    data object NeedsKey : SetupState

    data class Ready(val prefs: ReflectionPrefs) : SetupState
}

internal fun ReflectionError.toUserMessage(): String? = when (this) {
    ReflectionError.VaultNotLinked -> "Link your Obsidian vault to continue."
    ReflectionError.VaultPermissionDenied -> "Vault access was denied. Try linking your vault again."
    ReflectionError.ConsentRequired -> "Accept the privacy notice to use Daily Reflection."
    ReflectionError.KeyMissing -> "Add your OpenRouter API key to generate reflections."
    ReflectionError.DailyNoteMissing -> "No daily note found for today in your vault."
    ReflectionError.DraftMissing -> "No draft available to save."
    ReflectionError.InsufficientContent -> "Today's note doesn't have enough content for a reflection yet."
    ReflectionError.AlreadyExists -> "A reflection file already exists for today."
    ReflectionError.Network -> "Network error. Check your connection and try again."
    is ReflectionError.Provider -> message?.takeIf { it.isNotBlank() } ?: "The AI provider returned an error."
    ReflectionError.MalformedOutput -> "The AI response could not be used. Try generating again."
    ReflectionError.Cancelled -> null
    ReflectionError.OutsideWindow -> "Outside your auto-draft time window."
}
