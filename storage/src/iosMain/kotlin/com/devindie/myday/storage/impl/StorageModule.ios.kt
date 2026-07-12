package com.devindie.myday.storage.impl

import com.devindie.myday.storage.api.StorageConfig
import com.devindie.myday.storage.api.provider.StorageProvider
import org.koin.core.scope.Scope

internal actual fun Scope.defaultStorageProvider(config: StorageConfig): StorageProvider = IosDocumentStorageProvider()
