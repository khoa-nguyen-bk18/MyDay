package com.devindie.myday.data.reflection

import com.devindie.myday.data.coroutines.TestDispatcherProvider
import com.devindie.myday.data.coroutines.runDataTest
import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.domain.model.reflection.VaultLink
import com.devindie.myday.domain.reflection.EmbedLink
import com.devindie.myday.storage.api.StorageClient
import com.devindie.myday.storage.api.StorageEntry
import com.devindie.myday.storage.api.StorageError
import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StoragePickRequest
import com.devindie.myday.storage.api.StorageResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.TestScope
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ReflectionRepositoryImplTest {
    @Test
    fun generateMarkdown_delegatesToOpenRouter() = runDataTest { dispatchers ->
        val engine =
            MockEngine {
                respond(
                    content = """{"choices":[{"message":{"content":"## Today at a Glance\n\nGenerated"}}]}""",
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        val (_, repository) = createRepository(dispatchers, WritableFakeVaultStorageClient(emptyMap()), engine)

        val result = repository.generateMarkdown("journal body", "openai/gpt-4o-mini", "sk-test")

        assertTrue(result.getOrThrow().contains("Generated"))
    }

    @Test
    fun saveToVault_whenExistsAndNotReplace_returnsAlreadyExists() = runDataTest { dispatchers ->
        val storage =
            WritableFakeVaultStorageClient(
                mapOf(
                    CORE_DAILY_NOTES_CONFIG_PATH to """{"format":"YYYY-MM-DD","folder":"Daily"}""",
                    "Daily/2026-07-11.md" to "daily note",
                    "reflections/2026-07-11.md" to "existing reflection",
                ),
            )
        val (vaultLinkStore, repository) = createRepository(dispatchers, storage, MockEngine { respond("{}") })
        vaultLinkStore.linkVault()

        val result =
            repository.saveToVault(
                date = "2026-07-11",
                folder = "reflections",
                markdown = "# Reflection",
                replaceExistingFile = false,
            )

        assertTrue(result.isFailure)
        assertIs<ReflectionError.AlreadyExists>(result.exceptionOrNull())
    }

    @Test
    fun saveToVault_writesReflectionAndAppendsEmbed() = runDataTest { dispatchers ->
        val storage =
            WritableFakeVaultStorageClient(
                mapOf(
                    CORE_DAILY_NOTES_CONFIG_PATH to """{"format":"YYYY-MM-DD","folder":"Daily"}""",
                    "Daily/2026-07-11.md" to "daily note body",
                ),
            )
        val (vaultLinkStore, repository) = createRepository(dispatchers, storage, MockEngine { respond("{}") })
        vaultLinkStore.linkVault()

        val result =
            repository.saveToVault(
                date = "2026-07-11",
                folder = "reflections",
                markdown = "# Reflection\n\nSaved.",
                replaceExistingFile = false,
            )

        val document = result.getOrThrow()
        assertEquals("reflections/2026-07-11.md", document.relativePath)
        assertEquals("# Reflection\n\nSaved.", storage.files["reflections/2026-07-11.md"])
        val expectedDaily = EmbedLink.appendBlock("daily note body", "reflections", "2026-07-11")
        assertEquals(expectedDaily, storage.files["Daily/2026-07-11.md"])
    }

    private fun TestScope.createRepository(
        dispatchers: TestDispatcherProvider,
        storageClient: WritableFakeVaultStorageClient,
        engine: MockEngine,
    ): Pair<VaultLinkStore, ReflectionRepositoryImpl> {
        val vaultLinkStore =
            VaultLinkStore(
                dataStore = createTestPreferencesDataStore(backgroundScope, "reflection_repo"),
                dispatchers = dispatchers,
            )
        val dailyNotes =
            DailyNoteRepositoryImpl(
                vaultLinkStore = vaultLinkStore,
                vaultNotes = VaultNoteDataSource(storageClient),
                dispatchers = dispatchers,
            )
        val httpClient =
            HttpClient(engine) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
        val repository =
            ReflectionRepositoryImpl(
                openRouter = OpenRouterReflectionDataSource(httpClient, dispatchers),
                vaultLinkStore = vaultLinkStore,
                vaultNotes = VaultNoteDataSource(storageClient),
                dailyNotes = dailyNotes,
                dispatchers = dispatchers,
            )
        return vaultLinkStore to repository
    }

    private suspend fun VaultLinkStore.linkVault() {
        set(VaultLink("vault-token"))
    }
}

private class WritableFakeVaultStorageClient(initialFiles: Map<String, String>) : StorageClient {
    val files = initialFiles.toMutableMap()

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
    ): StorageResult<Unit> {
        files[relativePath] = text
        return StorageResult.Success(Unit)
    }

    override suspend fun writeBytes(
        token: StorageLocationToken,
        relativePath: String,
        bytes: ByteArray,
    ): StorageResult<Unit> = StorageResult.Failure(StorageError.NotConfigured)

    override suspend fun delete(token: StorageLocationToken, relativePath: String): StorageResult<Unit> =
        StorageResult.Failure(StorageError.NotConfigured)
}
