package com.devindie.myday.fake

import com.devindie.myday.analytics.api.AnalyticsClient

class FakeAnalyticsClient : AnalyticsClient {
    data class LoggedEvent(val name: String, val params: Map<String, Any>)

    val events = mutableListOf<LoggedEvent>()

    override fun logEvent(name: String, params: Map<String, Any>) {
        events += LoggedEvent(name, params)
    }

    override fun logScreen(screenName: String, screenClass: String?) = Unit

    override fun setUserProperty(name: String, value: String) = Unit

    override fun setUserId(userId: String?) = Unit

    override fun recordException(throwable: Throwable, message: String?) = Unit

    override fun log(message: String) = Unit
}
