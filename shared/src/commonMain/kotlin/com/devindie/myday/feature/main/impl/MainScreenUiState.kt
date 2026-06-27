package com.devindie.myday.feature.main.impl

import com.devindie.myday.core.constants.DEFAULT_STORE_NAME

/** UI contract for [MainScreen] — Stitch "Empty Nav Screen". */
data class MainScreenUiState(val storeName: String = DEFAULT_STORE_NAME)
