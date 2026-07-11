package com.devindie.myday.feature.dailyreflection.impl

import com.devindie.myday.analytics.api.AnalyticsClient
import com.devindie.myday.analytics.api.AnalyticsEvent
import com.devindie.myday.domain.model.reflection.NotHelpfulReason
import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.domain.model.reflection.ReflectionFeedbackEvent

internal object DailyReflectionAnalyticsEvents {
    const val OPENED = "daily_reflection_opened"
    const val GENERATION_STARTED = "daily_reflection_generation_started"
    const val GENERATION_COMPLETED = "daily_reflection_generation_completed"
    const val GENERATION_FAILED = "daily_reflection_generation_failed"
    const val GENERATION_CANCELLED = "daily_reflection_generation_cancelled"
    const val SAVED = "daily_reflection_saved"
    const val HELPFUL_SELECTED = "daily_reflection_helpful_selected"
    const val NOT_HELPFUL_SELECTED = "daily_reflection_not_helpful_selected"
    const val INSUFFICIENT_CONTENT = "daily_reflection_insufficient_content"
    const val CONSENT_ACCEPTED = "daily_reflection_privacy_consent_accepted"
    const val CONSENT_DECLINED = "daily_reflection_privacy_consent_declined"
}

/**
 * Builds analytics events for Daily Reflection.
 * Never include journal markdown, draft body, or source text in params.
 */
internal object DailyReflectionAnalytics {
    fun opened(): AnalyticsEvent = AnalyticsEvent(DailyReflectionAnalyticsEvents.OPENED)

    fun generationStarted(): AnalyticsEvent = AnalyticsEvent(DailyReflectionAnalyticsEvents.GENERATION_STARTED)

    fun generationCompleted(sourceTruncated: Boolean): AnalyticsEvent = AnalyticsEvent(
        DailyReflectionAnalyticsEvents.GENERATION_COMPLETED,
        mapOf("source_truncated" to sourceTruncated),
    )

    fun generationFailed(error: Throwable): AnalyticsEvent = AnalyticsEvent(
        DailyReflectionAnalyticsEvents.GENERATION_FAILED,
        mapOf("error_type" to errorType(error)),
    )

    fun generationCancelled(): AnalyticsEvent = AnalyticsEvent(DailyReflectionAnalyticsEvents.GENERATION_CANCELLED)

    fun saved(replaced: Boolean): AnalyticsEvent =
        AnalyticsEvent(DailyReflectionAnalyticsEvents.SAVED, mapOf("replaced" to replaced))

    fun feedback(event: ReflectionFeedbackEvent): AnalyticsEvent {
        val name =
            if (event.helpful) {
                DailyReflectionAnalyticsEvents.HELPFUL_SELECTED
            } else {
                DailyReflectionAnalyticsEvents.NOT_HELPFUL_SELECTED
            }
        val params =
            buildMap<String, Any> {
                event.reason?.let { put("reason", it) }
            }
        return AnalyticsEvent(name, params)
    }

    fun insufficientContent(): AnalyticsEvent = AnalyticsEvent(DailyReflectionAnalyticsEvents.INSUFFICIENT_CONTENT)

    fun consentAccepted(): AnalyticsEvent = AnalyticsEvent(DailyReflectionAnalyticsEvents.CONSENT_ACCEPTED)

    fun consentDeclined(): AnalyticsEvent = AnalyticsEvent(DailyReflectionAnalyticsEvents.CONSENT_DECLINED)

    fun feedbackParams(helpful: Boolean, reason: NotHelpfulReason?): Map<String, Any> = feedback(
        ReflectionFeedbackEvent(
            helpful = helpful,
            reason = reason?.name,
        ),
    ).params

    private fun errorType(error: Throwable): String = when (error) {
        is ReflectionError -> error::class.simpleName ?: "ReflectionError"
        else -> error::class.simpleName ?: "Unknown"
    }
}

internal fun AnalyticsClient.track(event: AnalyticsEvent) {
    logEvent(event.name, event.params)
}
