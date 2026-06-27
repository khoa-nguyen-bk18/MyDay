package com.devindie.myday.feature.apppromotion.api

interface AppPromotionClient {
    suspend fun requestInAppReview(): AppPromotionResult

    suspend fun shareApp(): AppPromotionResult
}
