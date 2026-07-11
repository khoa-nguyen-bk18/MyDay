package com.devindie.myday.core.di

import com.devindie.myday.apppromotion.appPromotionConfigForTemplate
import com.devindie.myday.domain.reflection.ReflectionInjection
import com.devindie.myday.domain.usecase.carddetail.GetCardDetailUseCase
import com.devindie.myday.domain.usecase.onboarding.CompleteOnboardingUseCase
import com.devindie.myday.domain.usecase.onboarding.HasCompletedOnboardingUseCase
import com.devindie.myday.domain.usecase.reflection.ClearOpenRouterKeyUseCase
import com.devindie.myday.domain.usecase.reflection.GenerateReflectionDraftUseCase
import com.devindie.myday.domain.usecase.reflection.GetTodayDraftUseCase
import com.devindie.myday.domain.usecase.reflection.LinkVaultUseCase
import com.devindie.myday.domain.usecase.reflection.ObserveReflectionSetupUseCase
import com.devindie.myday.domain.usecase.reflection.RunAutoDraftUseCase
import com.devindie.myday.domain.usecase.reflection.SaveReflectionUseCase
import com.devindie.myday.domain.usecase.reflection.SetOpenRouterKeyUseCase
import com.devindie.myday.domain.usecase.reflection.ShortenReflectionDraftUseCase
import com.devindie.myday.domain.usecase.reflection.SubmitReflectionFeedbackUseCase
import com.devindie.myday.domain.usecase.reflection.UpdateReflectionPrefsUseCase
import com.devindie.myday.domain.usecase.settings.GetSettingUseCase
import com.devindie.myday.domain.usecase.settings.ObserveSettingUseCase
import com.devindie.myday.domain.usecase.settings.ObserveSettingsScreenUseCase
import com.devindie.myday.domain.usecase.settings.UpdateSettingUseCase
import com.devindie.myday.domain.usecase.startup.InitializeAppUseCase
import com.devindie.myday.domain.usecase.user.ClearUserSessionUseCase
import com.devindie.myday.domain.usecase.user.GetUserSessionUseCase
import com.devindie.myday.domain.usecase.user.SaveUserSessionUseCase
import com.devindie.myday.feature.apppromotion.api.appPromotionFeatureModule
import com.devindie.myday.feature.browse.api.browseFeatureModule
import com.devindie.myday.feature.carddetail.api.cardDetailFeatureModule
import com.devindie.myday.feature.dailyreflection.api.dailyReflectionFeatureModule
import com.devindie.myday.feature.legal.api.legalFeatureModule
import com.devindie.myday.feature.main.api.mainFeatureModule
import com.devindie.myday.feature.onboarding.api.onboardingFeatureModule
import com.devindie.myday.feature.settings.api.settingsFeatureModule
import com.devindie.myday.feature.splash.api.splashFeatureModule
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
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
        factoryOf(::LinkVaultUseCase)
        factoryOf(::SetOpenRouterKeyUseCase)
        factoryOf(::ClearOpenRouterKeyUseCase)
        factoryOf(::ObserveReflectionSetupUseCase)
        factoryOf(::UpdateReflectionPrefsUseCase)
        factoryOf(::SubmitReflectionFeedbackUseCase)
        factory {
            GenerateReflectionDraftUseCase(
                dailyNotes = get(),
                drafts = get(),
                keys = get(),
                prefs = get(),
                reflections = get(),
                clock = get(named(ReflectionInjection.CLOCK)),
                todayIso = get(named(ReflectionInjection.TODAY_ISO)),
            )
        }
        factory {
            GetTodayDraftUseCase(
                drafts = get(),
                dailyNotes = get(),
                todayIso = get(named(ReflectionInjection.TODAY_ISO)),
            )
        }
        factory {
            RunAutoDraftUseCase(
                drafts = get(),
                prefs = get(),
                generate = get(),
                minuteOfDay = get(named(ReflectionInjection.MINUTE_OF_DAY)),
                todayIso = get(named(ReflectionInjection.TODAY_ISO)),
            )
        }
        factory {
            SaveReflectionUseCase(
                drafts = get(),
                prefs = get(),
                reflections = get(),
                todayIso = get(named(ReflectionInjection.TODAY_ISO)),
            )
        }
        factory {
            ShortenReflectionDraftUseCase(
                drafts = get(),
                keys = get(),
                prefs = get(),
                reflections = get(),
                todayIso = get(named(ReflectionInjection.TODAY_ISO)),
            )
        }
        includes(
            browseFeatureModule,
            cardDetailFeatureModule,
            mainFeatureModule,
            dailyReflectionFeatureModule,
            splashFeatureModule,
            onboardingFeatureModule,
            legalFeatureModule,
            settingsFeatureModule,
            appPromotionFeatureModule(appPromotionConfigForTemplate()),
        )
    }
