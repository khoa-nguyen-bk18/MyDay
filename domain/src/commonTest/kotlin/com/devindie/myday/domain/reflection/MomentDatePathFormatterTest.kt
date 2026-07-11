package com.devindie.myday.domain.reflection

import kotlin.test.Test
import kotlin.test.assertEquals

class MomentDatePathFormatterTest {
    @Test
    fun formatsNestedPath() {
        val path = MomentDatePathFormatter.format(
            folder = "Journal",
            format = "YYYY/MM/YYYY-MM-DD",
            year = 2026,
            month = 7,
            day = 11,
        )
        assertEquals("Journal/2026/07/2026-07-11.md", path)
    }

    @Test
    fun unsupportedToken_returnsNull() {
        val path = MomentDatePathFormatter.format(
            folder = "",
            format = "dddd",
            year = 2026,
            month = 7,
            day = 11,
        )
        assertEquals(null, path)
    }
}
