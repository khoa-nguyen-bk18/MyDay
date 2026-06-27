package com.devindie.myday.data.auth

/**
 * Persists OAuth-style tokens for the authenticated [io.ktor.client.HttpClient].
 *
 * @see com.devindie.myday.data.auth.KSafeTokenStore
 * @see com.devindie.myday.data.network.client.HttpClientFactory
 */
interface TokenStore {
    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun saveTokens(accessToken: String, refreshToken: String)

    suspend fun clear()
}
