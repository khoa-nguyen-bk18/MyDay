package com.devindie.myday.data.reflection

/**
 * Persists the user's OpenRouter BYOK key outside auth token storage.
 *
 * @see KSafeOpenRouterKeyStore
 * @see AiKeyRepositoryImpl
 */
interface OpenRouterKeyStore {
    suspend fun get(): String?

    suspend fun set(key: String)

    suspend fun clear()
}
