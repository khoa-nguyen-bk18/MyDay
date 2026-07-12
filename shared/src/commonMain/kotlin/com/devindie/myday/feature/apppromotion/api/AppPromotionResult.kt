package com.devindie.myday.feature.apppromotion.api

sealed interface AppPromotionResult {
    data object Success : AppPromotionResult

    data class Failure(val error: AppPromotionError) : AppPromotionResult
}

enum class AppPromotionError {
    NotConfigured,
    PlatformUnavailable,
    UserCancelled,
    Unknown,
}
