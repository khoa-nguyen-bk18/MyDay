package com.devindie.myday.data.reflection

import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.storage.api.StorageClient
import com.devindie.myday.storage.api.StorageError
import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StorageResult

class VaultNoteDataSource(private val storageClient: StorageClient) {
    suspend fun readTextIfExists(token: StorageLocationToken, relativePath: String): Result<String?> =
        when (val existsResult = storageClient.exists(token, relativePath)) {
            is StorageResult.Success ->
                if (!existsResult.value) {
                    Result.success(null)
                } else {
                    readText(token, relativePath)
                }
            is StorageResult.Failure -> Result.failure(mapStorageError(existsResult.error))
            StorageResult.Cancelled -> Result.failure(ReflectionError.Cancelled)
        }

    suspend fun readText(token: StorageLocationToken, relativePath: String): Result<String> =
        when (val result = storageClient.readText(token, relativePath)) {
            is StorageResult.Success -> Result.success(result.value)
            is StorageResult.Failure -> Result.failure(mapStorageError(result.error))
            StorageResult.Cancelled -> Result.failure(ReflectionError.Cancelled)
        }

    suspend fun writeText(token: StorageLocationToken, relativePath: String, text: String): Result<Unit> =
        when (val result = storageClient.writeText(token, relativePath, text)) {
            is StorageResult.Success -> Result.success(result.value)
            is StorageResult.Failure -> Result.failure(mapStorageError(result.error))
            StorageResult.Cancelled -> Result.failure(ReflectionError.Cancelled)
        }

    fun fileReader(token: StorageLocationToken): VaultFileReader = object : VaultFileReader {
        override suspend fun readTextOrNull(relativePath: String): String? =
            readTextIfExists(token, relativePath).getOrNull()
    }

    private fun mapStorageError(error: StorageError): ReflectionError = when (error) {
        StorageError.PermissionDenied -> ReflectionError.VaultPermissionDenied
        StorageError.NotFound -> ReflectionError.DailyNoteMissing
        is StorageError.InvalidPath,
        StorageError.NotConfigured,
        is StorageError.Io,
        -> ReflectionError.VaultPermissionDenied
    }
}
