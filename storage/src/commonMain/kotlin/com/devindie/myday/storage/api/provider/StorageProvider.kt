package com.devindie.myday.storage.api.provider

import com.devindie.myday.storage.api.StorageEntry
import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StorageResult

interface StorageProvider {
    suspend fun list(token: StorageLocationToken, relativePath: String): StorageResult<List<StorageEntry>>

    suspend fun exists(token: StorageLocationToken, relativePath: String): StorageResult<Boolean>

    suspend fun readBytes(token: StorageLocationToken, relativePath: String): StorageResult<ByteArray>

    suspend fun writeBytes(token: StorageLocationToken, relativePath: String, bytes: ByteArray): StorageResult<Unit>

    suspend fun delete(token: StorageLocationToken, relativePath: String): StorageResult<Unit>
}
