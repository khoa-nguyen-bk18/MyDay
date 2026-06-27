package com.devindie.myday.core.di

import com.devindie.myday.apppromotion.appPromotionConfigForTemplate
import com.devindie.myday.domain.usecase.carddetail.GetCardDetailUseCase
import com.devindie.myday.domain.usecase.onboarding.CompleteOnboardingUseCase
import com.devindie.myday.domain.usecase.onboarding.HasCompletedOnboardingUseCase
import com.devindie.myday.domain.usecase.startup.InitializeAppUseCase
import com.devindie.myday.domain.usecase.user.ClearUserSessionUseCase
import com.devindie.myday.domain.usecase.user.GetUserSessionUseCase
import com.devindie.myday.domain.usecase.user.SaveUserSessionUseCase
import com.devindie.myday.domain.usecase.settings.GetSettingUseCase
import com.devindie.myday.domain.usecase.settings.ObserveSettingUseCase
import com.devindie.myday.domain.usecase.settings.ObserveSettingsScreenUseCase
import com.devindie.myday.domain.usecase.settings.UpdateSettingUseCase
import com.devindie.myday.feature.apppromotion.api.appPromotionFeatureModule
import com.devindie.myday.feature.browse.api.browseFeatureModule
import com.devindie.myday.feature.carddetail.api.cardDetailFeatureModule
import com.devindie.myday.feature.collection.api.collectionFeatureModule
import com.devindie.myday.feature.main.api.mainFeatureModule
import com.devindie.myday.feature.onboarding.api.onboardingFeatureModule
import com.devindie.myday.feature.legal.api.legalFeatureModule
import com.devindie.myday.feature.settings.api.settingsFeatureModule
import com.devindie.myday.feature.splash.api.splashFeatureModule
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appDomainModule =
    module {
        factoryOf(::GetCardDetailUseCase)
        factoryOf(::InitializeAppUseCase)
        factoryOf(::GetUserSessionUseCase)
        factoryOf(::SaveUserSessionUseCase)
        factoryOf(::ClearUserSessionUseCase)
        factoryOf(::HasCompletedOnboardingUseCase)
        factoryOf(::CompleteOnboardingUseCase)
        factoryOf(::GetSettingUseCase)
        factory { ObserveSettingUseCase(get(), get()) }
        factory { ObserveSettingsScreenUseCase(get(), get()) }
        factory { UpdateSettingUseCase(get(), get()) }
        includes(
            browseFeatureModule,
            cardDetailFeatureModule,
            mainFeatureModule,
            collectionFeatureModule,
            splashFeatureModule,
            onboardingFeatureModule,
            legalFeatureModule,
            settingsFeatureModule,
            appPromotionFeatureModule(appPromotionConfigForTemplate()),
        )
    }
