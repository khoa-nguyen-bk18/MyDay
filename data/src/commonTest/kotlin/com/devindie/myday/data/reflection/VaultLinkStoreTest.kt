package com.devindie.myday.data.reflection

import com.devindie.myday.data.coroutines.runDataTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VaultLinkStoreTest {
    @Test
    fun get_returnsNullWhenUnset() = runDataTest { dispatchers ->
        val store =
            VaultLinkStore(
                dataStore = createTestPreferencesDataStore(backgroundScope, "vault_link"),
                dispatchers = dispatchers,
            )

        assertNull(store.get())
    }

    @Test
    fun setAndGet_roundTrip() = runDataTest { dispatchers ->
        val store =
            VaultLinkStore(
                dataStore = createTestPreferencesDataStore(backgroundScope, "vault_link"),
                dispatchers = dispatchers,
            )

        store.set(com.devindie.myday.domain.model.reflection.VaultLink("vault-token-123"))

        assertEquals("vault-token-123", store.get()?.tokenValue)
    }

    @Test
    fun clear_removesStoredLink() = runDataTest { dispatchers ->
        val store =
            VaultLinkStore(
                dataStore = createTestPreferencesDataStore(backgroundScope, "vault_link"),
                dispatchers = dispatchers,
            )

        store.set(com.devindie.myday.domain.model.reflection.VaultLink("vault-token-123"))
        store.clear()

        assertNull(store.get())
    }
}
