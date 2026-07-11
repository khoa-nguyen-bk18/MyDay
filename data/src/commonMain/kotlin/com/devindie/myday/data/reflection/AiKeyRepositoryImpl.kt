package com.devindie.myday.data.reflection

import com.devindie.myday.data.coroutines.DispatcherProvider
import com.devindie.myday.domain.repository.AiKeyRepository
import kotlinx.coroutines.withContext

class AiKeyRepositoryImpl(private val keyStore: OpenRouterKeyStore, private val dispatchers: DispatcherProvider) :
    AiKeyRepository {
    override suspend fun getOpenRouterKey(): String? = withContext(dispatchers.io) {
        keyStore.get()
    }

    override suspend fun setOpenRouterKey(key: String) {
        withContext(dispatchers.io) {
            keyStore.set(key)
        }
    }

    override suspend fun clearOpenRouterKey() {
        withContext(dispatchers.io) {
            keyStore.clear()
        }
    }
}
