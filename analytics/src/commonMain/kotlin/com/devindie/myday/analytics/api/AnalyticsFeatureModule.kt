package com.devindie.myday.analytics.api

import com.devindie.myday.analytics.impl.createAnalyticsModule
import org.koin.core.module.Module

fun analyticsFeatureModule(config: AnalyticsConfig): Module = createAnalyticsModule(config)
