package com.devindie.myday.data.reflection

import com.devindie.myday.data.coroutines.TestDispatcherProvider
import com.devindie.myday.data.coroutines.runDataTest
import com.devindie.myday.domain.model.reflection.Draft
import kotlinx.coroutines.test.TestScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DraftRepositoryImplTest {
    @Test
    fun get_returnsNullWhenUnset() = runDataTest { dispatchers ->
        val repository = createRepository(dispatchers)

        assertNull(repository.get("2026-07-11"))
    }

    @Test
    fun saveAndGet_roundTripPerDate() = runDataTest { dispatchers ->
        val repository = createRepository(dispatchers)
        val draft =
            Draft(
                date = "2026-07-11",
                markdown = "## Summary\nToday was productive.",
                sourceContentHash = "abc123",
                sourceTruncated = true,
                generatedAtEpochMs = 1_720_000_000_000L,
            )

        repository.save(draft)

        assertEquals(draft, repository.get("2026-07-11"))
    }

    @Test
    fun clear_removesOnlyRequestedDate() = runDataTest { dispatchers ->
        val repository = createRepository(dispatchers)
        val first =
            Draft(
                date = "2026-07-10",
                markdown = "first",
                sourceContentHash = "hash-1",
                sourceTruncated = false,
                generatedAtEpochMs = 1L,
            )
        val second =
            Draft(
                date = "2026-07-11",
                markdown = "second",
                sourceContentHash = "hash-2",
                sourceTruncated = false,
                generatedAtEpochMs = 2L,
            )

        repository.save(first)
        repository.save(second)
        repository.clear("2026-07-10")

        assertNull(repository.get("2026-07-10"))
        assertEquals(second, repository.get("2026-07-11"))
    }

    private fun TestScope.createRepository(dispatchers: TestDispatcherProvider): DraftRepositoryImpl =
        DraftRepositoryImpl(
            localDataSource =
            DraftLocalDataSource(
                dataStore = createTestPreferencesDataStore(backgroundScope, "reflection_drafts"),
            ),
            dispatchers = dispatchers,
        )
}
