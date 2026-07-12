package com.devindie.myday.storage.impl

import com.devindie.myday.storage.api.StorageLocationToken
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.BooleanVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.NSURLBookmarkResolutionWithSecurityScope
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class, ExperimentalEncodingApi::class)
internal object SecurityScopedBookmarkCodec {
    fun resolveUrl(token: StorageLocationToken): NSURL? {
        val bookmarkData = decodeBookmarkData(token.value) ?: return null

        return memScoped {
            val isStale = alloc<BooleanVar>()
            val url =
                NSURL.URLByResolvingBookmarkData(
                    bookmarkData = bookmarkData,
                    options = NSURLBookmarkResolutionWithSecurityScope,
                    relativeToURL = null,
                    bookmarkDataIsStale = isStale.ptr,
                    error = null,
                )
            if (url == null) {
                null
            } else {
                url
            }
        }
    }

    private fun decodeBookmarkData(value: String): NSData? = runCatching {
        val bytes = Base64.decode(value)
        IosNSDataFactory.fromByteArray(bytes)
    }.getOrNull()
}
