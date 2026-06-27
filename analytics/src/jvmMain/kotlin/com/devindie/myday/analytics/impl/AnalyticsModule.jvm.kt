package com.devindie.myday.analytics.impl

import com.devindie.myday.analytics.api.provider.CrashReportingProvider
import com.devindie.myday.analytics.api.provider.EventAnalyticsProvider
import com.devindie.myday.analytics.impl.provider.NoOpCrashReportingProvider
import com.devindie.myday.analytics.impl.provider.NoOpEventAnalyticsProvider
import org.koin.core.scope.Scope

internal actual fun Scope.defaultEventAnalyticsProvider(): EventAnalyticsProvider =
    NoOpEventAnalyticsProvider()

internal actual fun Scope.defaultCrashReportingProvider(): CrashReportingProvider =
    NoOpCrashReportingProvider()
