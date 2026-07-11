package com.devindie.myday.domain.reflection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EmbedLinkTest {
    @Test
    fun build_returnsWikiEmbedSyntax() {
        assertEquals(
            "![[reflections/2026-07-11]]",
            EmbedLink.build("reflections", "2026-07-11"),
        )
    }

    @Test
    fun contains_detectsExistingLink() {
        val body = "Some note content\n\n![[reflections/2026-07-11]]\n"
        assertTrue(EmbedLink.contains(body, "reflections", "2026-07-11"))
    }

    @Test
    fun contains_detectsLinkWithMdSuffix() {
        val body = "Some note content\n\n![[reflections/2026-07-11.md]]\n"
        assertTrue(EmbedLink.contains(body, "reflections", "2026-07-11"))
    }

    @Test
    fun appendBlock_isIdempotentWhenLinkAlreadyPresent() {
        val body = "Some note content\n\n![[reflections/2026-07-11]]\n"
        assertEquals(body, EmbedLink.appendBlock(body, "reflections", "2026-07-11"))
    }

    @Test
    fun appendBlock_addsBlockWhenLinkAbsent() {
        val body = "Some note content"
        assertEquals(
            "Some note content\n\n## Daily Reflection\n\n![[reflections/2026-07-11]]\n",
            EmbedLink.appendBlock(body, "reflections", "2026-07-11"),
        )
    }
}
