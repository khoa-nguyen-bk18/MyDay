package com.devindie.myday.data.reflection

import com.devindie.myday.domain.model.reflection.DailyNoteResolution
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObsidianDailyNoteResolverTest {
    @Test
    fun prefersPeriodicNotesOverCore() = runTest {
        val storage =
            FakeVaultFiles(
                mapOf(
                    PERIODIC_NOTES_CONFIG_PATH to
                        """
                        {"daily":{"format":"YYYY-MM-DD","folder":"periodic","enabled":true}}
                        """.trimIndent(),
                    CORE_DAILY_NOTES_CONFIG_PATH to """{"format":"YYYY-MM-DD","folder":"Daily"}""",
                    "periodic/2026-07-11.md" to "hello from periodic",
                ),
            )
        val resolver = ObsidianDailyNoteResolver(storage)
        val ref = resolver.resolve("2026-07-11")
        assertEquals("periodic/2026-07-11.md", ref.relativePath)
        assertEquals(DailyNoteResolution.PeriodicNotes, ref.resolution)
    }

    @Test
    fun usesCoreWhenPeriodicMissing() = runTest {
        val storage =
            FakeVaultFiles(
                mapOf(
                    CORE_DAILY_NOTES_CONFIG_PATH to """{"format":"YYYY-MM-DD","folder":"Daily"}""",
                    "Daily/2026-07-11.md" to "hello from core",
                ),
            )
        val resolver = ObsidianDailyNoteResolver(storage)
        val ref = resolver.resolve("2026-07-11")
        assertEquals("Daily/2026-07-11.md", ref.relativePath)
        assertEquals(DailyNoteResolution.CoreDailyNotes, ref.resolution)
    }

    @Test
    fun usesCoreWhenPeriodicDisabled() = runTest {
        val storage =
            FakeVaultFiles(
                mapOf(
                    PERIODIC_NOTES_CONFIG_PATH to
                        """
                        {"daily":{"format":"YYYY-MM-DD","folder":"periodic","enabled":false}}
                        """.trimIndent(),
                    CORE_DAILY_NOTES_CONFIG_PATH to """{"format":"YYYY-MM-DD","folder":"Daily"}""",
                ),
            )
        val resolver = ObsidianDailyNoteResolver(storage)
        val ref = resolver.resolve("2026-07-11")
        assertEquals("Daily/2026-07-11.md", ref.relativePath)
        assertEquals(DailyNoteResolution.CoreDailyNotes, ref.resolution)
    }

    @Test
    fun usesFallbackWhenConfigsMissing() = runTest {
        val storage = FakeVaultFiles(emptyMap())
        val resolver = ObsidianDailyNoteResolver(storage)
        val ref = resolver.resolve("2026-07-11")
        assertEquals("2026-07-11.md", ref.relativePath)
        assertEquals(DailyNoteResolution.Fallback, ref.resolution)
    }

    @Test
    fun fallsBackWhenFormatUnsupported() = runTest {
        val storage =
            FakeVaultFiles(
                mapOf(
                    PERIODIC_NOTES_CONFIG_PATH to
                        """
                        {"daily":{"format":"dddd","folder":"periodic","enabled":true}}
                        """.trimIndent(),
                    CORE_DAILY_NOTES_CONFIG_PATH to """{"format":"dddd","folder":"Daily"}""",
                ),
            )
        val resolver = ObsidianDailyNoteResolver(storage)
        val ref = resolver.resolve("2026-07-11")
        assertEquals("2026-07-11.md", ref.relativePath)
        assertEquals(DailyNoteResolution.Fallback, ref.resolution)
    }

    @Test
    fun fallsBackToCoreWhenPeriodicFormatUnsupported() = runTest {
        val storage =
            FakeVaultFiles(
                mapOf(
                    PERIODIC_NOTES_CONFIG_PATH to
                        """
                        {"daily":{"format":"dddd","folder":"periodic","enabled":true}}
                        """.trimIndent(),
                    CORE_DAILY_NOTES_CONFIG_PATH to """{"format":"YYYY-MM-DD","folder":"Daily"}""",
                ),
            )
        val resolver = ObsidianDailyNoteResolver(storage)
        val ref = resolver.resolve("2026-07-11")
        assertEquals("Daily/2026-07-11.md", ref.relativePath)
        assertEquals(DailyNoteResolution.CoreDailyNotes, ref.resolution)
    }
}

private class FakeVaultFiles(private val files: Map<String, String>) : VaultFileReader {
    override suspend fun readTextOrNull(relativePath: String): String? = files[relativePath]
}
