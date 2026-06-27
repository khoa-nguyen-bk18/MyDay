package com.devindie.myday.billing

import com.devindie.myday.billing.api.BillingConfig
import com.devindie.myday.billing.api.billingFeatureModule
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import org.koin.core.module.Module

fun billingConfigForAndroid(
    enabled: Boolean,
    apiKey: String,
): BillingConfig =
    BillingConfig(
        enabled = enabled && apiKey.isNotBlank(),
        revenueCatApiKeyAndroid = apiKey,
    )

fun billingKoinModuleForAndroid(
    enabled: Boolean,
    apiKey: String,
): Module = billingFeatureModule(billingConfigForAndroid(enabled, apiKey))

fun configureBillingPlatform(apiKey: String) {
    if (apiKey.isNotBlank()) {
        Purchases.configure(
            PurchasesConfiguration(apiKey = apiKey) {},
        )
    }
}
