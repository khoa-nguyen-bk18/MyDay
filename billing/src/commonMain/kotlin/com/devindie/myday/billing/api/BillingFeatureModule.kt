package com.devindie.myday.billing.api

import com.devindie.myday.billing.impl.createBillingModule
import org.koin.core.module.Module

fun billingFeatureModule(config: BillingConfig): Module = createBillingModule(config)
