package com.devindie.myday.feature.apppromotion.impl.platform

import com.devindie.myday.feature.apppromotion.api.AppPromotionConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun appPromotionPlatformModule(): Module = module {
    single<AppPromotionPlatform> {
        AndroidAppPromotionPlatform(
            applicationContext = androidContext(),
            config = get<AppPromotionConfig>(),
        )
    }
}
