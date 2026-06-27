package com.devindie.myday.apppromotion

import com.devindie.myday.feature.apppromotion.api.AppPromotionConfig

fun appPromotionConfigForTemplate(): AppPromotionConfig =
    AppPromotionConfig(
        enabled = true,
        appDisplayName = "Cmp Template",
        playStoreUrl = "https://play.google.com/store/apps/details?id=com.devindie.myday",
        appStoreUrl = "https://apps.apple.com/app/id000000000",
    )
