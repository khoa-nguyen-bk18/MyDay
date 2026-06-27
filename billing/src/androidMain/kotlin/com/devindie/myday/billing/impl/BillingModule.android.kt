package com.devindie.myday.billing.impl

import com.devindie.myday.billing.api.BillingConfig
import com.devindie.myday.billing.api.provider.BillingProvider
import com.devindie.myday.billing.impl.provider.RevenueCatBillingProvider
import org.koin.core.scope.Scope

internal actual fun Scope.defaultBillingProvider(config: BillingConfig): BillingProvider = RevenueCatBillingProvider()
