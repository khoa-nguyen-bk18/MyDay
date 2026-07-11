package com.devindie.myday.domain.repository

import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import kotlinx.coroutines.flow.Flow

interface ReflectionPrefsRepository {
    fun observe(): Flow<ReflectionPrefs>

    suspend fun get(): ReflectionPrefs

    suspend fun update(transform: (ReflectionPrefs) -> ReflectionPrefs)
}
