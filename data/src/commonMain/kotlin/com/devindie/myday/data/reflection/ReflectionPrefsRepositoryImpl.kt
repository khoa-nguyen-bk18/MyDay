package com.devindie.myday.data.reflection

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.devindie.myday.data.coroutines.DispatcherProvider
import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import com.devindie.myday.domain.repository.ReflectionPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ReflectionPrefsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val dispatchers: DispatcherProvider,
) : ReflectionPrefsRepository {
    override fun observe(): Flow<ReflectionPrefs> = dataStore.data.map { preferences ->
        preferences.toReflectionPrefs()
    }

    override suspend fun get(): ReflectionPrefs = withContext(dispatchers.io) {
        dataStore.data.first().toReflectionPrefs()
    }

    override suspend fun update(transform: (ReflectionPrefs) -> ReflectionPrefs) {
        withContext(dispatchers.io) {
            dataStore.edit { preferences ->
                preferences.writeReflectionPrefs(transform(preferences.toReflectionPrefs()))
            }
        }
    }
}
