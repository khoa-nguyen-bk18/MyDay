package com.devindie.myday

import com.devindie.myday.analytics.api.AnalyticsConfig
import com.devindie.myday.analytics.api.analyticsFeatureModule
import com.devindie.myday.billing.api.billingFeatureModule
import com.devindie.myday.billing.billingConfigForIos
import com.devindie.myday.browsePagingModule
import com.devindie.myday.core.di.startKoinApp
import com.devindie.myday.data.di.platformDataModule
import com.devindie.myday.domain.reflection.ReflectionInjection
import com.devindie.myday.domain.usecase.reflection.EnsureReflectionScheduleUseCase
import com.devindie.myday.settings.settingsCatalogModule
import com.devindie.myday.storage.storageKoinModuleForIos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin

fun doInitKoin() {
    startKoinApp(
        appModules =
        listOf(
            platformDataModule(),
            settingsCatalogModule(),
            browsePagingModule,
            analyticsFeatureModule(AnalyticsConfig(enabled = true)),
            billingFeatureModule(billingConfigForIos()),
            storageKoinModuleForIos(),
            module {
                single(named(ReflectionInjection.DEBUG_TOOLS)) { false }
            },
        ),
    )
    CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
        runCatching { getKoin().get<EnsureReflectionScheduleUseCase>()() }
    }
}
