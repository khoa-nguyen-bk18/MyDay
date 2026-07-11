package com.devindie.myday.data.reflection

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.devindie.myday.data.coroutines.DispatcherProvider
import com.devindie.myday.domain.model.reflection.VaultLink
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private val VAULT_LINK_TOKEN_KEY = stringPreferencesKey("vault_link_token")

class VaultLinkStore(private val dataStore: DataStore<Preferences>, private val dispatchers: DispatcherProvider) {
    suspend fun get(): VaultLink? = withContext(dispatchers.io) {
        dataStore.data.first()[VAULT_LINK_TOKEN_KEY]?.let(::VaultLink)
    }

    suspend fun set(link: VaultLink) {
        withContext(dispatchers.io) {
            dataStore.edit { preferences ->
                preferences[VAULT_LINK_TOKEN_KEY] = link.tokenValue
            }
        }
    }

    suspend fun clear() {
        withContext(dispatchers.io) {
            dataStore.edit { preferences ->
                preferences.remove(VAULT_LINK_TOKEN_KEY)
            }
        }
    }
}
