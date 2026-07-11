package com.devindie.myday.data.reflection

import eu.anifantakis.lib.ksafe.KSafe

const val OPENROUTER_API_KEY_STORAGE_KEY: String = "openrouter_api_key"

class KSafeOpenRouterKeyStore(private val ksafe: KSafe) : OpenRouterKeyStore {
    override suspend fun get(): String? =
        ksafe.get(OPENROUTER_API_KEY_STORAGE_KEY, OpenRouterApiKey()).value.takeIf { it.isNotEmpty() }

    override suspend fun set(key: String) {
        ksafe.put(OPENROUTER_API_KEY_STORAGE_KEY, OpenRouterApiKey(value = key))
    }

    override suspend fun clear() {
        ksafe.put(OPENROUTER_API_KEY_STORAGE_KEY, OpenRouterApiKey())
    }
}
