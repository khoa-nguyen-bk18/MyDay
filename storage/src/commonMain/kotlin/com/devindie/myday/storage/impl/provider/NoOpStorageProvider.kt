package com.devindie.myday.storage.impl.provider

import com.devindie.myday.storage.api.StorageEntry
import com.devindie.myday.storage.api.StorageError
import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StorageResult
import com.devindie.myday.storage.api.provider.StorageProvider

internal class NoOpStorageProvider : StorageProvider {
    override suspend fun list(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<List<StorageEntry>> = notConfigured()

    override suspend fun exists(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<Boolean> = notConfigured()

    override suspend fun readBytes(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<ByteArray> = notConfigured()

    override suspend fun writeBytes(
        token: StorageLocationToken,
        relativePath: String,
        bytes: ByteArray,
    ): StorageResult<Unit> = notConfigured()

    override suspend fun delete(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<Unit> = notConfigured()

    private fun <T> notConfigured(): StorageResult<T> =
        StorageResult.Failure(StorageError.NotConfigured)
}
