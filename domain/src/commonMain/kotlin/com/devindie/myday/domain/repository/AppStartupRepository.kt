package com.devindie.myday.domain.repository

interface AppStartupRepository {
    suspend fun ensureReady(): Result<Unit>
}
