package com.devindie.myday.storage.impl

import com.devindie.myday.storage.api.StorageEntry
import com.devindie.myday.storage.api.StorageError
import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StorageResult
import com.devindie.myday.storage.api.provider.StorageProvider
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileModificationDate
import platform.Foundation.NSFileSize
import platform.Foundation.NSFileType
import platform.Foundation.NSFileTypeDirectory
import platform.Foundation.NSNumber
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsDirectoryKey
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.timeIntervalSince1970
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal class IosDocumentStorageProvider : StorageProvider {
    override suspend fun list(token: StorageLocationToken, relativePath: String): StorageResult<List<StorageEntry>> =
        withIo(token) { rootUrl ->
            val directoryUrl =
                resolveChildUrl(rootUrl, relativePath)
                    ?: return@withIo StorageResult.Failure(StorageError.NotFound)
            if (!directoryUrl.isDirectory()) {
                return@withIo StorageResult.Failure(StorageError.InvalidPath(relativePath))
            }
            @Suppress("UNCHECKED_CAST")
            val contents =
                NSFileManager.defaultManager.contentsOfDirectoryAtURL(
                    url = directoryUrl,
                    includingPropertiesForKeys = listOf(NSURLIsDirectoryKey),
                    options = 0u,
                    error = null,
                ) as? List<NSURL> ?: return@withIo StorageResult.Success(emptyList())

            StorageResult.Success(
                contents.mapNotNull { childUrl ->
                    val name = childUrl.lastPathComponent ?: return@mapNotNull null
                    val isDirectory = childUrl.isDirectory()
                    val attributes =
                        NSFileManager.defaultManager.attributesOfItemAtPath(
                            path = childUrl.path.orEmpty(),
                            error = null,
                        )
                    StorageEntry(
                        name = name,
                        relativePath =
                        if (relativePath.isEmpty()) {
                            name
                        } else {
                            "$relativePath/$name"
                        },
                        isDirectory = isDirectory,
                        sizeBytes = attributes?.fileSize(),
                        lastModifiedEpochMillis = attributes?.lastModifiedEpochMillis(),
                    )
                },
            )
        }

    override suspend fun exists(token: StorageLocationToken, relativePath: String): StorageResult<Boolean> =
        withIo(token) { rootUrl ->
            val targetUrl = resolveChildUrl(rootUrl, relativePath) ?: return@withIo StorageResult.Success(false)
            val exists =
                NSFileManager.defaultManager.fileExistsAtPath(
                    path = targetUrl.path.orEmpty(),
                )
            StorageResult.Success(exists)
        }

    override suspend fun readBytes(token: StorageLocationToken, relativePath: String): StorageResult<ByteArray> =
        withIo(token) { rootUrl ->
            val fileUrl =
                resolveChildUrl(rootUrl, relativePath)
                    ?: return@withIo StorageResult.Failure(StorageError.NotFound)
            if (fileUrl.isDirectory()) {
                return@withIo StorageResult.Failure(StorageError.InvalidPath(relativePath))
            }
            val data =
                NSData.dataWithContentsOfURL(fileUrl)
                    ?: return@withIo StorageResult.Failure(StorageError.PermissionDenied)
            StorageResult.Success(data.toByteArray())
        }

    override suspend fun writeBytes(
        token: StorageLocationToken,
        relativePath: String,
        bytes: ByteArray,
    ): StorageResult<Unit> = withIo(token) { rootUrl ->
        val (parentPath, fileName) = splitRelativePath(relativePath)
        val parentUrl =
            resolveChildUrl(rootUrl, parentPath)
                ?: return@withIo StorageResult.Failure(StorageError.NotFound)
        if (!parentUrl.isDirectory()) {
            return@withIo StorageResult.Failure(StorageError.NotFound)
        }
        val fileUrl =
            parentUrl.URLByAppendingPathComponent(fileName, isDirectory = false)
                ?: return@withIo StorageResult.Failure(
                    StorageError.Io(message = "unable_to_resolve_file_url"),
                )
        val data = IosNSDataFactory.fromByteArray(bytes)
        val path =
            fileUrl.path
                ?: return@withIo StorageResult.Failure(StorageError.PermissionDenied)
        val wrote =
            NSFileManager.defaultManager.createFileAtPath(
                path = path,
                contents = data,
                attributes = null,
            )
        if (!wrote) {
            return@withIo StorageResult.Failure(StorageError.PermissionDenied)
        }
        StorageResult.Success(Unit)
    }

    override suspend fun delete(token: StorageLocationToken, relativePath: String): StorageResult<Unit> =
        withIo(token) { rootUrl ->
            val targetUrl =
                resolveChildUrl(rootUrl, relativePath)
                    ?: return@withIo StorageResult.Failure(StorageError.NotFound)
            val deleted =
                NSFileManager.defaultManager.removeItemAtURL(
                    URL = targetUrl,
                    error = null,
                )
            if (!deleted) {
                return@withIo StorageResult.Failure(StorageError.PermissionDenied)
            }
            StorageResult.Success(Unit)
        }

    private suspend fun <T> withIo(token: StorageLocationToken, block: (NSURL) -> StorageResult<T>): StorageResult<T> =
        withContext(Dispatchers.IO) {
            val rootUrl =
                SecurityScopedBookmarkCodec.resolveUrl(token)
                    ?: return@withContext StorageResult.Failure(StorageError.PermissionDenied)
            withScopedAccess(rootUrl) {
                block(rootUrl)
            }
        }

    private inline fun <T> withScopedAccess(url: NSURL, block: () -> T): T {
        val started = url.startAccessingSecurityScopedResource()
        try {
            return block()
        } finally {
            if (started) {
                url.stopAccessingSecurityScopedResource()
            }
        }
    }

    private fun resolveChildUrl(rootUrl: NSURL, relativePath: String): NSURL? {
        if (relativePath.isEmpty()) {
            return rootUrl
        }
        var current = rootUrl
        for (segment in relativePath.split('/')) {
            if (segment.isEmpty()) {
                return null
            }
            current =
                current.URLByAppendingPathComponent(
                    pathComponent = segment,
                    isDirectory = false,
                ) ?: return null
        }
        return current
    }

    private fun NSURL.isDirectory(): Boolean {
        val attributes =
            NSFileManager.defaultManager.attributesOfItemAtPath(
                path = path.orEmpty(),
                error = null,
            )
        return attributes?.get(NSFileType) as? String == NSFileTypeDirectory
    }

    private fun Map<Any?, *>.fileSize(): Long? = (this[NSFileSize] as? NSNumber)?.longLongValue

    private fun Map<Any?, *>.lastModifiedEpochMillis(): Long? {
        val date = this[NSFileModificationDate] as? NSDate ?: return null
        return (date.timeIntervalSince1970 * 1_000).toLong()
    }

    private fun NSData.toByteArray(): ByteArray {
        val length = this.length.toInt()
        if (length == 0) {
            return ByteArray(0)
        }
        return ByteArray(length).apply {
            usePinned { pinned ->
                memcpy(pinned.addressOf(0), bytes, this@toByteArray.length)
            }
        }
    }

    private companion object {
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
