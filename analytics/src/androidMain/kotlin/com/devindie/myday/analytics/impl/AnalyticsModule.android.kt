package com.devindie.myday.analytics.impl

import android.content.Context
import com.devindie.myday.analytics.api.provider.CrashReportingProvider
import com.devindie.myday.analytics.api.provider.EventAnalyticsProvider
import com.devindie.myday.analytics.impl.firebase.FirebaseCrashReportingProvider
import com.devindie.myday.analytics.impl.firebase.FirebaseEventAnalyticsProvider
import org.koin.core.scope.Scope

internal actual fun Scope.defaultEventAnalyticsProvider(): EventAnalyticsProvider {
    val context = get<Context>()
    return FirebaseEventAnalyticsProvider(context)
}

internal actual fun Scope.defaultCrashReportingProvider(): CrashReportingProvider =
    FirebaseCrashReportingProvider()
