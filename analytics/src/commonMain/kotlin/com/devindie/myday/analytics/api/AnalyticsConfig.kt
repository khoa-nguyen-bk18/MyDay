package com.devindie.myday.analytics.api

import com.devindie.myday.analytics.api.provider.CrashReportingProvider
import com.devindie.myday.analytics.api.provider.EventAnalyticsProvider

data class AnalyticsConfig(
    val enabled: Boolean = true,
    val eventProvider: EventAnalyticsProvider? = null,
    val crashProvider: CrashReportingProvider? = null,
)
