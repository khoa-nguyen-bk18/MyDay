package com.devindie.myday.domain.repository

interface AiKeyRepository {
    suspend fun getOpenRouterKey(): String?

    suspend fun setOpenRouterKey(key: String)

    suspend fun clearOpenRouterKey()
}
