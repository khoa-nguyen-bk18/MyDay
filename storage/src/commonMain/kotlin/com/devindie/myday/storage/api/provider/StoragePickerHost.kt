package com.devindie.myday.storage.api.provider

import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StoragePickRequest
import com.devindie.myday.storage.api.StorageResult

interface StoragePickerHost {
    suspend fun pickFolder(request: StoragePickRequest): StorageResult<StorageLocationToken>
}
