package com.devindie.myday.storage

import android.content.ContentResolver
import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StoragePickRequest
import com.devindie.myday.storage.api.StorageResult
import com.devindie.myday.storage.api.provider.StoragePickerHost

class AndroidStoragePickerHost(
    private val contentResolver: ContentResolver,
) : StoragePickerHost {
    override suspend fun pickFolder(request: StoragePickRequest): StorageResult<StorageLocationToken> {
        val uri = DocumentTreePickerRegistry.pickTree() ?: return StorageResult.Cancelled
        persistTreeGrant(contentResolver, uri, request.accessMode)
        return StorageResult.Success(StorageLocationToken(uri.toString()))
    }
}
