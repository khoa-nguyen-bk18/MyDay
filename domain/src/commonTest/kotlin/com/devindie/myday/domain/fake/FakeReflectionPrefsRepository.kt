package com.devindie.myday.domain.fake

import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import com.devindie.myday.domain.repository.ReflectionPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeReflectionPrefsRepository(initial: ReflectionPrefs = ReflectionPrefs()) : ReflectionPrefsRepository {
    private val state = MutableStateFlow(initial)

    override fun observe(): Flow<ReflectionPrefs> = state

    override suspend fun get(): ReflectionPrefs = state.value

    override suspend fun update(transform: (ReflectionPrefs) -> ReflectionPrefs) {
        state.update(transform)
    }
}
