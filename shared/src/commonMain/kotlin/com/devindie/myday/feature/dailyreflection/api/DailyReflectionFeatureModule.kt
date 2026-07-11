package com.devindie.myday.feature.dailyreflection.api

import com.devindie.myday.domain.reflection.ReflectionInjection
import com.devindie.myday.feature.dailyreflection.impl.DailyReflectionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dailyReflectionFeatureModule =
    module {
        viewModel {
            DailyReflectionViewModel(
                observeReflectionSetup = get(),
                getReflectionSetup = get(),
                getTodayDraft = get(),
                linkVault = get(),
                vaultPicker = get(),
                updateReflectionPrefs = get(),
                setOpenRouterKey = get(),
                clearOpenRouterKey = get(),
                generateDraft = get(),
                shortenDraft = get(),
                saveReflection = get(),
                runAutoDraft = get(),
                submitFeedback = get(),
                analytics = get(),
                debugToolsEnabled =
                getOrNull<Boolean>(named(ReflectionInjection.DEBUG_TOOLS)) ?: false,
            )
        }
    }
