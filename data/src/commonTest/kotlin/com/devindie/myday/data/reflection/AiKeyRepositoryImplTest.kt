package com.devindie.myday.data.reflection

import com.devindie.myday.data.coroutines.runDataTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AiKeyRepositoryImplTest {
    @Test
    fun openRouterApiKey_emptyByDefault() {
        val key = OpenRouterApiKey()

        assertEquals("", key.value)
    }

    @Test
    fun emptyKeyString_mapsToNullForStoreContract() {
        val stored = OpenRouterApiKey().value.takeIf { it.isNotEmpty() }

        assertNull(stored)
    }

    @Test
    fun setGetAndClear_roundTrip() = runDataTest { dispatchers ->
        val keyStore = InMemoryOpenRouterKeyStore()
        val repository = AiKeyRepositoryImpl(keyStore = keyStore, dispatchers = dispatchers)

        repository.setOpenRouterKey("sk-or-test-key")

        assertEquals("sk-or-test-key", repository.getOpenRouterKey())

        repository.clearOpenRouterKey()

        assertNull(repository.getOpenRouterKey())
    }
}

private class InMemoryOpenRouterKeyStore : OpenRouterKeyStore {
    private var key: String? = null

    override suspend fun get(): String? = key

    override suspend fun set(key: String) {
        this.key = key
    }

    override suspend fun clear() {
        key = null
    }
}
