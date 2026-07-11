package com.devindie.myday.feature.dailyreflection.impl

import com.devindie.myday.domain.model.reflection.NotHelpfulReason
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DailyReflectionAnalyticsTest {
    @Test
    fun feedbackParams_haveNoMarkdownOrSourceKeys() {
        val helpful = DailyReflectionAnalytics.feedbackParams(helpful = true, reason = null)
        assertFalse(helpful.containsKey("markdown"))
        assertFalse(helpful.containsKey("source"))

        val notHelpful =
            DailyReflectionAnalytics.feedbackParams(
                helpful = false,
                reason = NotHelpfulReason.TooLong,
            )
        assertTrue(notHelpful.containsKey("reason"))
        assertFalse(notHelpful.containsKey("markdown"))
        assertFalse(notHelpful.containsKey("source"))
        assertFalse(notHelpful.keys.any { it.contains("body", ignoreCase = true) })
    }
}
