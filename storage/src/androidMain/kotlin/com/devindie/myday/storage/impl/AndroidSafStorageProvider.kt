package com.devindie.myday.storage.impl

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.devindie.myday.storage.api.StorageEntry
import com.devindie.myday.storage.api.StorageError
import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StorageResult
import com.devindie.myday.storage.api.provider.StorageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AndroidSafStorageProvider(
    private val context: Context,
) : StorageProvider {
    override suspend fun list(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<List<StorageEntry>> =
        withIo {
            val directory =
                resolveDirectory(token, relativePath)
                    ?: return@withIo StorageResult.Failure(StorageError.NotFound)
            val children = directory.listFiles().toList()
            StorageResult.Success(
                children.mapNotNull { child ->
                    val name = child.name ?: return@mapNotNull null
                    StorageEntry(
                        name = name,
                        relativePath =
                            if (relativePath.isEmpty()) {
                                name
                            } else {
                                "$relativePath/$name"
                            },
                        isDirectory = child.isDirectory,
                        sizeBytes = if (child.isDirectory) null else child.length(),
                        lastModifiedEpochMillis = child.lastModified(),
                    )
                },
            )
        }

    override suspend fun exists(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<Boolean> =
        withIo {
            StorageResult.Success(resolveDocument(token, relativePath) != null)
        }

    override suspend fun readBytes(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<ByteArray> =
        withIo {
            val document =
                resolveDocument(token, relativePath)
                    ?: return@withIo StorageResult.Failure(StorageError.NotFound)
            if (document.isDirectory) {
                return@withIo StorageResult.Failure(StorageError.InvalidPath(relativePath))
            }
            val bytes =
                context.contentResolver.openInputStream(document.uri)?.use { input ->
                    input.readBytes()
                } ?: return@withIo StorageResult.Failure(StorageError.PermissionDenied)
            StorageResult.Success(bytes)
        }

    override suspend fun writeBytes(
        token: StorageLocationToken,
        relativePath: String,
        bytes: ByteArray,
    ): StorageResult<Unit> =
        withIo {
            val (parentPath, fileName) = splitRelativePath(relativePath)
            val parent =
                resolveDirectory(token, parentPath)
                    ?: return@withIo StorageResult.Failure(StorageError.NotFound)
            if (!parent.canWrite()) {
                return@withIo StorageResult.Failure(StorageError.PermissionDenied)
            }
            val document =
                parent.findFile(fileName)
                    ?: parent.createFile(MIME_TYPE_OCTET_STREAM, fileName)
                    ?: return@withIo StorageResult.Failure(
                        StorageError.Io(message = "unable_to_create_file"),
                    )
            context.contentResolver.openOutputStream(document.uri, WRITE_MODE_TRUNCATE)?.use { output ->
                output.write(bytes)
            } ?: return@withIo StorageResult.Failure(StorageError.PermissionDenied)
            StorageResult.Success(Unit)
        }

    override suspend fun delete(
        token: StorageLocationToken,
        relativePath: String,
    ): StorageResult<Unit> =
        withIo {
            val document =
                resolveDocument(token, relativePath)
                    ?: return@withIo StorageResult.Failure(StorageError.NotFound)
            if (!document.delete()) {
                return@withIo StorageResult.Failure(StorageError.PermissionDenied)
            }
            StorageResult.Success(Unit)
        }

    private fun resolveDirectory(
        token: StorageLocationToken,
        relativePath: String,
    ): DocumentFile? {
        val document = resolveDocument(token, relativePath) ?: return null
        return document.takeIf { it.isDirectory }
    }

    private fun resolveDocument(
        token: StorageLocationToken,
        relativePath: String,
    ): DocumentFile? {
        val treeUri = SafUriCodec.parseTreeUri(token) ?: return null
        var current = DocumentFile.fromTreeUri(context, treeUri) ?: return null
        if (relativePath.isEmpty()) {
            return current
        }
        for (segment in relativePath.split('/')) {
            if (segment.isEmpty()) {
                return null
            }
            current = current.findFile(segment) ?: return null
        }
        return current
    }

    private suspend fun <T> withIo(block: () -> StorageResult<T>): StorageResult<T> =
        withContext(Dispatchers.IO) {
            block()
        }

    private companion object {
        const val MIME_TYPE_OCTET_STREAM = "application/octet-stream"
        const val WRITE_MODE_TRUNCATE = "wt"

        fun splitRelativePath(relativePath: String): Pair<String, String> {
            val lastSlash = relativePath.lastIndexOf('/')
            return if (lastSlash < 0) {
                "" to relativePath
            } else {
                relativePath.substring(0, lastSlash) to relativePath.substring(lastSlash + 1)
            }
        }
    }
}
