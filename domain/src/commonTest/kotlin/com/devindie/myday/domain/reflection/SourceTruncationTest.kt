package com.devindie.myday.domain.reflection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SourceTruncationTest {
    @Test
    fun textUnderMax_returnsSameTextNotTruncated() {
        val text = "hello world"
        val result = SourceTruncation.fromEnd(text, maxChars = 100)
        assertEquals(text, result.text)
        assertFalse(result.truncated)
    }

    @Test
    fun textOverMax_takesLastMaxCharsAndMarksTruncated() {
        val text = "abcdefghij"
        val result = SourceTruncation.fromEnd(text, maxChars = 5)
        assertEquals("fghij", result.text)
        assertTrue(result.truncated)
    }
}
