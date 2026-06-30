package com.devindie.myday.storage.impl

import com.devindie.myday.storage.api.StorageClient
import com.devindie.myday.storage.api.StorageConfig
import com.devindie.myday.storage.api.provider.StorageProvider
import com.devindie.myday.storage.impl.provider.NoOpStorageProvider
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.module

internal fun createStorageModule(config: StorageConfig): Module {
    require(!config.enabled || config.pickerHost != null) {
        "StorageConfig.pickerHost is required when enabled = true"
    }

    return module {
        single<StorageClient> {
            val provider: StorageProvider =
                when {
                    !config.enabled -> NoOpStorageProvider()
                    config.provider != null -> config.provider
                    else -> defaultStorageProvider(config)
                }
            StorageClientImpl(
                provider = provider,
                pickerHost = config.pickerHost,
                enabled = config.enabled,
            )
        }
    }
}

internal expect fun Scope.defaultStorageProvider(config: StorageConfig): StorageProvider
