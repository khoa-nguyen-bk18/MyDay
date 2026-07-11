package com.devindie.myday.fake

import com.devindie.myday.domain.repository.AiKeyRepository

class FakeAiKeyRepository(private var key: String? = null) : AiKeyRepository {
    override suspend fun getOpenRouterKey(): String? = key

    override suspend fun setOpenRouterKey(key: String) {
        this.key = key
    }

    override suspend fun clearOpenRouterKey() {
        key = null
    }
}
