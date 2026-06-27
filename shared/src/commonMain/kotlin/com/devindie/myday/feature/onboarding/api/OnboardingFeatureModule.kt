package com.devindie.myday.feature.onboarding.api

import com.devindie.myday.feature.onboarding.impl.OnboardingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val onboardingFeatureModule =
    module {
        viewModelOf(::OnboardingViewModel)
    }
