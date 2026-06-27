package com.devindie.myday.feature.legal.impl

internal data class LegalDocumentScreenUiState(
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val reloadToken: Int = 0,
)
