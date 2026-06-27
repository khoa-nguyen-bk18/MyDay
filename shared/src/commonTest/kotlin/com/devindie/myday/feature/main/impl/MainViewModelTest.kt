package com.devindie.myday.feature.main.impl

import com.devindie.myday.core.constants.DEFAULT_STORE_NAME
import kotlin.test.Test
import kotlin.test.assertEquals

class MainViewModelTest {
    @Test
    fun initialState_hasDefaultStoreName() {
        val viewModel = MainViewModel()

        assertEquals(DEFAULT_STORE_NAME, viewModel.uiState.value.storeName)
    }
}
