package com.devindie.myday.domain.model.reflection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class ReflectionModelsTest {
    @Test
    fun reflectionPrefs_defaultsMatchConstants() {
        val prefs = ReflectionPrefs()

        assertFalse(prefs.consentAccepted)
        assertFalse(prefs.featureEnabled)
        assertEquals(ReflectionConstants.DEFAULT_WINDOW_START_MINUTE, prefs.windowStartMinuteOfDay)
        assertEquals(ReflectionConstants.DEFAULT_WINDOW_END_MINUTE, prefs.windowEndMinuteOfDay)
        assertEquals(ReflectionConstants.DEFAULT_REFLECTION_FOLDER, prefs.reflectionFolder)
        assertNull(prefs.modelOverride)
        assertFalse(prefs.usedFallbackPathNoticeShown)
    }

    @Test
    fun draft_andDailyNoteRef_constructWithExpectedValues() {
        val ref = DailyNoteRef(
            date = "2026-07-11",
            relativePath = "daily/2026-07-11.md",
            resolution = DailyNoteResolution.PeriodicNotes,
        )
        val draft = Draft(
            date = "2026-07-11",
            markdown = "# Reflection",
            sourceContentHash = "abc123",
            sourceTruncated = false,
            generatedAtEpochMs = 1_720_000_000_000L,
        )

        assertEquals("2026-07-11", ref.date)
        assertEquals("daily/2026-07-11.md", ref.relativePath)
        assertEquals(DailyNoteResolution.PeriodicNotes, ref.resolution)
        assertEquals("2026-07-11", draft.date)
        assertEquals("# Reflection", draft.markdown)
        assertEquals("abc123", draft.sourceContentHash)
        assertFalse(draft.sourceTruncated)
        assertEquals(1_720_000_000_000L, draft.generatedAtEpochMs)
    }
}
