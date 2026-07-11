package com.devindie.myday.data.reflection

import com.devindie.myday.data.coroutines.TestDispatcherProvider
import com.devindie.myday.data.coroutines.runDataTest
import com.devindie.myday.domain.model.reflection.DailyNoteResolution
import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.domain.model.reflection.VaultLink
import com.devindie.myday.storage.api.StorageClient
import com.devindie.myday.storage.api.StorageEntry
import com.devindie.myday.storage.api.StorageError
import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StoragePickRequest
import com.devindie.myday.storage.api.StorageResult
import kotlinx.coroutines.test.TestScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DailyNoteRepositoryImplTest {
    @Test
    fun resolveAndRead_returnsContentWhenFileExists() = runDataTest { dispatchers ->
        val storage =
            FakeVaultStorageClient(
                mapOf(
                    CORE_DAILY_NOTES_CONFIG_PATH to """{"format":"YYYY-MM-DD","folder":"Daily"}""",
                    "Daily/2026-07-11.md" to "journal body",
                ),
            )
        val repository = createRepository(dispatchers, storage)
        repository.setVaultLink(VaultLink("vault-token"))

        val result = repository.resolveAndRead("2026-07-11")

        val content = result.getOrThrow()
        assertEquals("journal body", content?.body)
        assertEquals("Daily/2026-07-11.md", content?.ref?.relativePath)
        assertEquals(DailyNoteResolution.CoreDailyNotes, content?.ref?.resolution)
        assertEquals(dailyNoteContentHash("journal body"), content?.contentHash)
    }

    @Test
    fun resolveAndRead_returnsNullWhenFileMissing() = runDataTest { dispatchers ->
        val storage =
            FakeVaultStorageClient(
                mapOf(
                    CORE_DAILY_NOTES_CONFIG_PATH to """{"format":"YYYY-MM-DD","folder":"Daily"}""",
                ),
            )
        val repository = createRepository(dispatchers, storage)
        repository.setVaultLink(VaultLink("vault-token"))

        val result = repository.resolveAndRead("2026-07-11")

        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
    }

    @Test
    fun resolveAndRead_failsWhenVaultNotLinked() = runDataTest { dispatchers ->
        val repository = createRepository(dispatchers, FakeVaultStorageClient(emptyMap()))

        val result = repository.resolveAndRead("2026-07-11")

        assertTrue(result.isFailure)
        assertIs<ReflectionError.VaultNotLinked>(result.exceptionOrNull())
    }

    @Test
    fun vaultLink_roundTripsThroughStore() = runDataTest { dispatchers ->
        val repository = createRepository(dispatchers, FakeVaultStorageClient(emptyMap()))

        repository.setVaultLink(VaultLink("vault-token"))
        assertEquals("vault-token", repository.getVaultLink()?.tokenValue)

        repository.clearVaultLink()
        assertNull(repository.getVaultLink())
    }

    private fun TestScope.createRepository(
        dispatchers: TestDispatcherProvider,
        storageClient: StorageClient,
    ): DailyNoteRepositoryImpl = DailyNoteRepositoryImpl(
        vaultLinkStore =
        VaultLinkStore(
            dataStore = createTestPreferencesDataStore(backgroundScope, "daily_note_repo"),
            dispatchers = dispatchers,
        ),
        vaultNotes = VaultNoteDataSource(storageClient),
        dispatchers = dispatchers,
    )
}

private class FakeVaultStorageClient(private val files: Map<String, String>) : StorageClient {
    override suspend fun pickFolder(request: StoragePickRequest): StorageResult<StorageLocationToken> =
        StorageResult.Failure(StorageError.NotConfigured)

    override suspend fun list(token: StorageLocationToken, relativePath: String): StorageResult<List<StorageEntry>> =
        StorageResult.Failure(StorageError.NotConfigured)

    override suspend fun exists(token: StorageLocationToken, relativePath: String): StorageResult<Boolean> =
        StorageResult.Success(relativePath in files)

    override suspend fun readText(token: StorageLocationToken, relativePath: String): StorageResult<String> =
        files[relativePath]?.let { StorageResult.Success(it) }
            ?: StorageResult.Failure(StorageError.NotFound)

    override suspend fun readBytes(token: StorageLocationToken, relativePath: String): StorageResult<ByteArray> =
        StorageResult.Failure(StorageError.NotConfigured)

    override suspend fun writeText(
        token: StorageLocationToken,
        relativePath: String,
        text: String,
    ): StorageResult<Unit> = StorageResult.Failure(StorageError.NotConfigured)

    override suspend fun writeBytes(
        token: StorageLocationToken,
        relativePath: String,
        bytes: ByteArray,
    ): StorageResult<Unit> = StorageResult.Failure(StorageError.NotConfigured)

    override suspend fun delete(token: StorageLocationToken, relativePath: String): StorageResult<Unit> =
        StorageResult.Failure(StorageError.NotConfigured)
}
