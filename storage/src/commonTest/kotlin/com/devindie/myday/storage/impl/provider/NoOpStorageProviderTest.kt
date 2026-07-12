package com.devindie.myday.storage.impl.provider

import com.devindie.myday.storage.api.StorageError
import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StorageResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NoOpStorageProviderTest {
    private val provider = NoOpStorageProvider()
    private val token = StorageLocationToken("token")

    @Test
    fun list_returnsNotConfigured() = runTest {
        val result = provider.list(token, "") as StorageResult.Failure
        assertTrue(result.error is StorageError.NotConfigured)
    }

    @Test
    fun exists_returnsNotConfigured() = runTest {
        val result = provider.exists(token, "file.txt") as StorageResult.Failure
        assertTrue(result.error is StorageError.NotConfigured)
    }

    @Test
    fun readBytes_returnsNotConfigured() = runTest {
        val result = provider.readBytes(token, "file.txt") as StorageResult.Failure
        assertTrue(result.error is StorageError.NotConfigured)
    }

    @Test
    fun writeBytes_returnsNotConfigured() = runTest {
        val result = provider.writeBytes(token, "file.txt", byteArrayOf()) as StorageResult.Failure
        assertTrue(result.error is StorageError.NotConfigured)
    }

    @Test
    fun delete_returnsNotConfigured() = runTest {
        val result = provider.delete(token, "file.txt") as StorageResult.Failure
        assertEquals(StorageError.NotConfigured, result.error)
    }
}
