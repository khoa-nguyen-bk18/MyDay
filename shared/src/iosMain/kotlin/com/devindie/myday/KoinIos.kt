package com.devindie.myday

import com.devindie.myday.analytics.api.AnalyticsConfig
import com.devindie.myday.analytics.api.analyticsFeatureModule
import com.devindie.myday.billing.api.billingFeatureModule
import com.devindie.myday.billing.billingConfigForIos
import com.devindie.myday.browsePagingModule
import com.devindie.myday.core.di.startKoinApp
import com.devindie.myday.data.di.platformDataModule
import com.devindie.myday.settings.settingsCatalogModule
import com.devindie.myday.storage.storageKoinModuleForIos

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
        ),
    )
}
