package com.devindie.myday

import android.app.Application
import com.devindie.myday.analytics.api.AnalyticsConfig
import com.devindie.myday.analytics.api.analyticsFeatureModule
import com.devindie.myday.billing.billingKoinModuleForAndroid
import com.devindie.myday.billing.configureBillingPlatform
import com.devindie.myday.browsePagingModule
import com.devindie.myday.core.di.startKoinApp
import com.devindie.myday.data.di.platformDataModule
import com.devindie.myday.domain.reflection.ReflectionInjection
import com.devindie.myday.domain.usecase.reflection.EnsureReflectionScheduleUseCase
import com.devindie.myday.settings.settingsCatalogModule
import com.devindie.myday.storage.AndroidStoragePickerHost
import com.devindie.myday.storage.api.StorageConfig
import com.devindie.myday.storage.api.storageFeatureModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

class MyDayApplication : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        val billingEnabled =
            BuildConfig.BILLING_ENABLED && BuildConfig.REVENUECAT_API_KEY_ANDROID.isNotBlank()
        if (billingEnabled) {
            configureBillingPlatform(apiKey = BuildConfig.REVENUECAT_API_KEY_ANDROID)
        }

        startKoinApp(
            appModules =
            listOf(
                platformDataModule(),
                settingsCatalogModule(),
                browsePagingModule,
                analyticsFeatureModule(
                    AnalyticsConfig(
                        enabled = !BuildConfig.DEBUG,
                    ),
                ),
                billingKoinModuleForAndroid(
                    enabled = BuildConfig.BILLING_ENABLED,
                    apiKey = BuildConfig.REVENUECAT_API_KEY_ANDROID,
                ),
                storageFeatureModule(
                    StorageConfig(
                        enabled = true,
                        pickerHost = AndroidStoragePickerHost(contentResolver),
                    ),
                ),
                module {
                    single(named(ReflectionInjection.DEBUG_TOOLS)) { BuildConfig.DEBUG }
                },
            ),
        ) {
            androidContext(this@MyDayApplication)
        }

        appScope.launch {
            runCatching { get<EnsureReflectionScheduleUseCase>()() }
        }
    }
}
