package com.devindie.myday.fake

import com.devindie.myday.feature.apppromotion.api.AppPromotionClient
import com.devindie.myday.feature.apppromotion.api.AppPromotionResult

class FakeAppPromotionClient(
    var reviewResult: AppPromotionResult = AppPromotionResult.Success,
    var shareResult: AppPromotionResult = AppPromotionResult.Success,
) : AppPromotionClient {
    var reviewCallCount = 0
        private set
    var shareCallCount = 0
        private set

    override suspend fun requestInAppReview(): AppPromotionResult {
        reviewCallCount++
        return reviewResult
    }

    override suspend fun shareApp(): AppPromotionResult {
        shareCallCount++
        return shareResult
    }
}
