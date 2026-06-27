package com.devindie.myday.feature.settings.api

import com.devindie.myday.feature.settings.impl.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsFeatureModule =
    module {
        viewModelOf(::SettingsViewModel)
    }
