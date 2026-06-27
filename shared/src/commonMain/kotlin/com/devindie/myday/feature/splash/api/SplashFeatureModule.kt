package com.devindie.myday.feature.splash.api

import com.devindie.myday.feature.splash.impl.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val splashFeatureModule =
    module {
        viewModelOf(::SplashViewModel)
    }
