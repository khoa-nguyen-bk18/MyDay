package com.devindie.myday.storage.impl

import com.devindie.myday.storage.api.StorageClient
import com.devindie.myday.storage.api.StorageEntry
import com.devindie.myday.storage.api.StorageError
import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StoragePickRequest
import com.devindie.myday.storage.api.StorageResult
import com.devindie.myday.storage.api.provider.StoragePickerHost
import com.devindie.myday.storage.api.provider.StorageProvider

internal class StorageClientImpl(
    private val provider: StorageProvider,
    private val pickerHost: StoragePickerHost?,
    private val enabled: Boolean,
) : StorageClient {
    override suspend fun pickFolder(request: StoragePickRequest): StorageResult<StorageLocationToken> {
        if (!enabled) {
            return StorageResult.Failure(StorageError.NotConfigured)
        }
        val host = pickerHost ?: return StorageResult.Failure(StorageError.NotConfigured)
        return runSafely { host.pickFolder(request) }
    }

    override suspend fun list(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<List<StorageEntry>> =
        runForPath(relativePath) {
            provider.list(token, relativePath)
        }

    override suspend fun exists(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<Boolean> =
        runForPath(relativePath) {
            provider.exists(token, relativePath)
        }

    override suspend fun readText(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<String> =
        when (val result = readBytes(token, relativePath)) {
            is StorageResult.Success -> StorageResult.Success(result.value.decodeToString())
            is StorageResult.Failure -> result
            is StorageResult.Cancelled -> StorageResult.Cancelled
        }

    override suspend fun readBytes(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<ByteArray> =
        runForPath(relativePath) {
            provider.readBytes(token, relativePath)
        }

    override suspend fun writeText(
        token: StorageLocationToken,
        relativePath: String,
        text: String,
    ): StorageResult<Unit> = writeBytes(token, relativePath, text.encodeToByteArray())

    override suspend fun writeBytes(
        token: StorageLocationToken,
        relativePath: String,
        bytes: ByteArray,
    ): StorageResult<Unit> =
        runForPath(relativePath) {
            provider.writeBytes(token, relativePath, bytes)
        }

    override suspend fun delete(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<Unit> =
        runForPath(relativePath) {
            provider.delete(token, relativePath)
        }

    private suspend inline fun <T> runForPath(
        relativePath: String,
        crossinline block: suspend () -> StorageResult<T>,
    ): StorageResult<T> {
        StoragePathValidator.validate(relativePath)?.let { error ->
            return StorageResult.Failure(error)
        }
        return runSafely(block)
    }

    private suspend inline fun <T> runSafely(
        crossinline block: suspend () -> StorageResult<T>,
    ): StorageResult<T> =
        try {
            block()
        } catch (@Suppress("TooGenericExceptionCaught") error: Exception) {
            StorageResult.Failure(
                StorageError.Io(
                    message = error.message ?: "storage_error",
                    cause = error,
                ),
            )
        }
}
