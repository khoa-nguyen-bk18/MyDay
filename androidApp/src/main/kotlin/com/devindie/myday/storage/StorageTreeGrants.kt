package com.devindie.myday.storage

import android.content.ContentResolver
import android.net.Uri
import com.devindie.myday.storage.api.StorageAccessMode

internal fun persistTreeGrant(contentResolver: ContentResolver, uri: Uri, mode: StorageAccessMode) {
    contentResolver.takePersistableUriPermission(
        uri,
        persistableUriFlags(mode),
    )
}
