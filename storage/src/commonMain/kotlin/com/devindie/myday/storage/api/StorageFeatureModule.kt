package com.devindie.myday.storage.api

import com.devindie.myday.storage.impl.createStorageModule
import org.koin.core.module.Module

fun storageFeatureModule(config: StorageConfig): Module = createStorageModule(config)
