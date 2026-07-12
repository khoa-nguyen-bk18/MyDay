package com.devindie.myday.feature.settings.impl

import com.devindie.myday.domain.model.settings.BooleanSettingsItemModel
import com.devindie.myday.domain.model.settings.SettingKey
import com.devindie.myday.domain.model.settings.SettingValue
import com.devindie.myday.domain.usecase.settings.ObserveSettingsScreenUseCase
import com.devindie.myday.domain.usecase.settings.UpdateSettingUseCase
import com.devindie.myday.fake.FakeSettingsCatalog
import com.devindie.myday.fake.FakeSettingsRepository
import com.devindie.myday.test.advanceMainUntilIdle
import com.devindie.myday.test.runViewModelTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SettingsViewModelTest {
    @Test
    fun emitsScreenItemsFromCatalog() = runViewModelTest {
        val viewModel =
            SettingsViewModel(
                observeSettingsScreen = ObserveSettingsScreenUseCase(FakeSettingsRepository(), FakeSettingsCatalog()),
                updateSetting = UpdateSettingUseCase(FakeSettingsRepository(), FakeSettingsCatalog()),
            )
        advanceMainUntilIdle()

        val item = viewModel.uiState.value.sections.single().items.first()
        assertIs<BooleanSettingsItemModel>(item)
        assertEquals(true, item.value)
    }

    @Test
    fun onSettingChanged_updatesValue() = runViewModelTest {
        val repository = FakeSettingsRepository()
        val catalog = FakeSettingsCatalog()
        val viewModel =
            SettingsViewModel(
                observeSettingsScreen = ObserveSettingsScreenUseCase(repository, catalog),
                updateSetting = UpdateSettingUseCase(repository, catalog),
            )
        advanceMainUntilIdle()

        viewModel.onSettingChanged(
            SettingKey("general.notifications"),
            SettingValue.BooleanValue(false),
        )
        advanceMainUntilIdle()

        val item = viewModel.uiState.value.sections.single().items.first() as BooleanSettingsItemModel
        assertEquals(false, item.value)
    }
}
