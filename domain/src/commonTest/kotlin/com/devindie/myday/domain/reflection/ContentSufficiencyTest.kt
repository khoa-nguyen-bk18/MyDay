package com.devindie.myday.domain.reflection

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContentSufficiencyTest {
    @Test
    fun empty_isInsufficient() {
        assertFalse(ContentSufficiency.isSufficient(""))
    }

    @Test
    fun headingsAndEmptyTasksOnly_isInsufficient() {
        val text = """
            # 2026-07-11
            ## Tasks
            - [ ]
            - [ ]
        """.trimIndent()
        assertFalse(ContentSufficiency.isSufficient(text))
    }

    @Test
    fun longProse_isSufficient() {
        assertTrue(ContentSufficiency.isSufficient("x".repeat(200)))
    }
}
