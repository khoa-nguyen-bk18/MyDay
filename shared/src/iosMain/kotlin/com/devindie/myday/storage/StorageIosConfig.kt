package com.devindie.myday.storage

import com.devindie.myday.storage.api.StorageConfig
import com.devindie.myday.storage.api.storageFeatureModule
import org.koin.core.module.Module

fun storageKoinModuleForIos(): Module = storageFeatureModule(
    StorageConfig(
        enabled = true,
        pickerHost = IosStoragePickerHost(),
    ),
)
