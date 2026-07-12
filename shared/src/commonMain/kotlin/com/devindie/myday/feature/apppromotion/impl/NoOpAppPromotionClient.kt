package com.devindie.myday.feature.apppromotion.impl

import com.devindie.myday.feature.apppromotion.api.AppPromotionClient
import com.devindie.myday.feature.apppromotion.api.AppPromotionError
import com.devindie.myday.feature.apppromotion.api.AppPromotionResult

internal class NoOpAppPromotionClient : AppPromotionClient {
    override suspend fun requestInAppReview(): AppPromotionResult =
        AppPromotionResult.Failure(AppPromotionError.NotConfigured)

    override suspend fun shareApp(): AppPromotionResult = AppPromotionResult.Failure(AppPromotionError.NotConfigured)
}
