package com.devindie.myday.billing.api

import com.devindie.myday.billing.api.provider.BillingProvider

data class BillingConfig(
    val enabled: Boolean = false,
    val revenueCatApiKeyAndroid: String = "",
    val revenueCatApiKeyIos: String = "",
    val provider: BillingProvider? = null,
)
