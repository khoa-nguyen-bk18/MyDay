package com.devindie.myday.feature.apppromotion.impl.platform

import com.devindie.myday.feature.apppromotion.api.AppPromotionResult

internal interface AppPromotionPlatform {
    suspend fun requestInAppReview(): AppPromotionResult

    suspend fun shareApp(): AppPromotionResult
}
