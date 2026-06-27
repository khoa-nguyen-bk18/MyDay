package com.devindie.myday.feature.main.api

import com.devindie.myday.feature.main.impl.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainFeatureModule =
    module {
        viewModelOf(::MainViewModel)
    }
