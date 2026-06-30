package com.devindie.myday.storage

import android.net.Uri
import com.devindie.myday.storage.api.StorageAccessMode
import kotlinx.coroutines.CompletableDeferred

internal object DocumentTreePickerRegistry {
    private var pickTree: (suspend () -> Uri?)? = null

    fun register(pickTree: suspend () -> Uri?) {
        this.pickTree = pickTree
    }

    suspend fun pickTree(): Uri? = pickTree?.invoke()
}

internal fun persistableUriFlags(mode: StorageAccessMode): Int {
    val read = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
    val write = android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    return when (mode) {
        StorageAccessMode.Read -> read
        StorageAccessMode.Write -> write
        StorageAccessMode.ReadWrite -> read or write
    }
}
