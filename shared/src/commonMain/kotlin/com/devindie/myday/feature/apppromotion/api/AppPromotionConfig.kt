package com.devindie.myday.feature.apppromotion.api

data class AppPromotionConfig(
    val enabled: Boolean = true,
    val appDisplayName: String,
    val playStoreUrl: String,
    val appStoreUrl: String,
    val shareMessage: String? = null,
) {
    fun resolvedShareMessage(): String = shareMessage ?: "Check out $appDisplayName!"
}
