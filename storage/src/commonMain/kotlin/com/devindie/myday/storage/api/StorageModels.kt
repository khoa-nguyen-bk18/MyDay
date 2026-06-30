package com.devindie.myday.storage.api

enum class StorageAccessMode {
    Read,
    Write,
    ReadWrite,
}

data class StoragePickRequest(
    val accessMode: StorageAccessMode,
)

data class StorageLocationToken(val value: String)

data class StorageEntry(
    val name: String,
    val relativePath: String,
    val isDirectory: Boolean,
    val sizeBytes: Long?,
    val lastModifiedEpochMillis: Long?,
)

sealed interface StorageResult<out T> {
    data class Success<T>(val value: T) : StorageResult<T>

    data class Failure(val error: StorageError) : StorageResult<Nothing>

    data object Cancelled : StorageResult<Nothing>
}

sealed interface StorageError {
    data object NotConfigured : StorageError

    data object PermissionDenied : StorageError

    data object NotFound : StorageError

    data class InvalidPath(val relativePath: String) : StorageError

    data class Io(val message: String, val cause: Throwable? = null) : StorageError
}
