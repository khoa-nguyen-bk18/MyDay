package com.devindie.myday.storage.api

import com.devindie.myday.storage.api.provider.StoragePickerHost
import com.devindie.myday.storage.api.provider.StorageProvider

data class StorageConfig(
    val enabled: Boolean = false,
    val pickerHost: StoragePickerHost? = null,
    val provider: StorageProvider? = null,
)
