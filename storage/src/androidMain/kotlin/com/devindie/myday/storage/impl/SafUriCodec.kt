package com.devindie.myday.storage.impl

import android.net.Uri
import com.devindie.myday.storage.api.StorageLocationToken

internal object SafUriCodec {
    fun parseTreeUri(token: StorageLocationToken): Uri? = runCatching { Uri.parse(token.value) }
        .getOrNull()
        ?.takeIf { it.toString().isNotBlank() }
}
