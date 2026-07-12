package com.devindie.myday.storage.api

interface StorageClient {
    suspend fun pickFolder(request: StoragePickRequest): StorageResult<StorageLocationToken>

    suspend fun list(token: StorageLocationToken, relativePath: String = ""): StorageResult<List<StorageEntry>>

    suspend fun exists(token: StorageLocationToken, relativePath: String): StorageResult<Boolean>

    suspend fun readText(token: StorageLocationToken, relativePath: String): StorageResult<String>

    suspend fun readBytes(token: StorageLocationToken, relativePath: String): StorageResult<ByteArray>

    suspend fun writeText(token: StorageLocationToken, relativePath: String, text: String): StorageResult<Unit>

    suspend fun writeBytes(token: StorageLocationToken, relativePath: String, bytes: ByteArray): StorageResult<Unit>

    suspend fun delete(token: StorageLocationToken, relativePath: String): StorageResult<Unit>
}
