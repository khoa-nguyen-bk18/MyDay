package com.devindie.myday.storage.impl

import com.devindie.myday.storage.api.StorageAccessMode
import com.devindie.myday.storage.api.StorageEntry
import com.devindie.myday.storage.api.StorageError
import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StoragePickRequest
import com.devindie.myday.storage.api.StorageResult
import com.devindie.myday.storage.api.provider.StoragePickerHost
import com.devindie.myday.storage.api.provider.StorageProvider
import com.devindie.myday.storage.impl.provider.NoOpStorageProvider
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StorageClientImplTest {
    @Test
    fun pickFolder_delegatesToHost() =
        runTest {
            val host = FakeStoragePickerHost()
            val client =
                StorageClientImpl(
                    provider = FakeStorageProvider(),
                    pickerHost = host,
                    enabled = true,
                )

            val result = client.pickFolder(StoragePickRequest(StorageAccessMode.ReadWrite))

            assertEquals(StorageAccessMode.ReadWrite, host.lastRequest?.accessMode)
            assertEquals(StorageLocationToken("fake-root"), (result as StorageResult.Success).value)
        }

    @Test
    fun pickFolder_whenDisabled_returnsNotConfigured() =
        runTest {
            val client =
                StorageClientImpl(
                    provider = FakeStorageProvider(),
                    pickerHost = FakeStoragePickerHost(),
                    enabled = false,
                )

            val result = client.pickFolder(StoragePickRequest(StorageAccessMode.Read)) as StorageResult.Failure

            assertEquals(StorageError.NotConfigured, result.error)
        }

    @Test
    fun readText_roundTripsUtf8() =
        runTest {
            val provider = FakeStorageProvider()
            val client =
                StorageClientImpl(
                    provider = provider,
                    pickerHost = null,
                    enabled = true,
                )
            val token = StorageLocationToken("root")

            client.writeText(token, "notes/hello.txt", "héllo")
            val result = client.readText(token, "notes/hello.txt") as StorageResult.Success

            assertEquals("héllo", result.value)
        }

    @Test
    fun invalidPath_shortCircuitsWithoutCallingProvider() =
        runTest {
            val provider = FakeStorageProvider()
            val client =
                StorageClientImpl(
                    provider = provider,
                    pickerHost = null,
                    enabled = true,
                )

            val result =
                client.readBytes(
                    StorageLocationToken("root"),
                    "../secret",
                ) as StorageResult.Failure

            assertTrue(result.error is StorageError.InvalidPath)
            assertEquals(0, provider.readBytesCallCount)
        }

    @Test
    fun providerThrows_returnsIoFailure() =
        runTest {
            val provider =
                object : StorageProvider by FakeStorageProvider() {
                    override suspend fun readBytes(
                        token: StorageLocationToken,
                        relativePath: String,
                    ): StorageResult<ByteArray> = error("disk failure")
                }
            val client =
                StorageClientImpl(
                    provider = provider,
                    pickerHost = null,
                    enabled = true,
                )

            val result =
                client.readBytes(
                    StorageLocationToken("root"),
                    "file.txt",
                ) as StorageResult.Failure

            assertTrue(result.error is StorageError.Io)
            assertEquals("disk failure", (result.error as StorageError.Io).message)
        }

    @Test
    fun disabledProvider_usesNoOp() =
        runTest {
            val client =
                StorageClientImpl(
                    provider = NoOpStorageProvider(),
                    pickerHost = null,
                    enabled = false,
                )

            val result =
                client.list(
                    StorageLocationToken("root"),
                ) as StorageResult.Failure

            assertEquals(StorageError.NotConfigured, result.error)
        }
}

private class FakeStoragePickerHost(
    private val token: StorageLocationToken = StorageLocationToken("fake-root"),
) : StoragePickerHost {
    var lastRequest: StoragePickRequest? = null

    override suspend fun pickFolder(request: StoragePickRequest): StorageResult<StorageLocationToken> {
        lastRequest = request
        return StorageResult.Success(token)
    }
}

private class FakeStorageProvider(
    private val files: MutableMap<String, ByteArray> = mutableMapOf(),
) : StorageProvider {
    var readBytesCallCount: Int = 0

    override suspend fun list(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<List<StorageEntry>> {
        val prefix = pathPrefix(relativePath)
        val entries =
            files.keys
                .filter { it.startsWith(prefix) }
                .mapNotNull { path ->
                    val remainder = path.removePrefix(prefix)
                    if (remainder.isEmpty() || '/' in remainder.dropLastWhile { it != '/' }) {
                        return@mapNotNull null
                    }
                    val name = remainder.substringBefore('/')
                    StorageEntry(
                        name = name,
                        relativePath = if (relativePath.isEmpty()) name else "$relativePath/$name",
                        isDirectory = false,
                        sizeBytes = files[path]?.size?.toLong(),
                        lastModifiedEpochMillis = null,
                    )
                }.distinctBy { it.relativePath }
        return StorageResult.Success(entries)
    }

    override suspend fun exists(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<Boolean> = StorageResult.Success(relativePath in files)

    override suspend fun readBytes(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<ByteArray> {
        readBytesCallCount++
        val bytes = files[relativePath] ?: return StorageResult.Failure(StorageError.NotFound)
        return StorageResult.Success(bytes)
    }

    override suspend fun writeBytes(
        token: StorageLocationToken,
        relativePath: String,
        bytes: ByteArray,
    ): StorageResult<Unit> {
        files[relativePath] = bytes
        return StorageResult.Success(Unit)
    }

    override suspend fun delete(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<Unit> {
        files.remove(relativePath)
        return StorageResult.Success(Unit)
    }

    private fun pathPrefix(relativePath: String): String =
        if (relativePath.isEmpty()) {
            ""
        } else {
            "$relativePath/"
        }
}
