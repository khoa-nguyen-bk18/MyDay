package com.devindie.myday.data.reflection

import com.devindie.myday.data.coroutines.runDataTest
import com.devindie.myday.domain.model.reflection.ReflectionConstants
import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import kotlinx.coroutines.flow.first
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReflectionPrefsRepositoryImplTest {
    @Test
    fun get_returnsDefaultsWhenUnset() = runDataTest { dispatchers ->
        val repository =
            ReflectionPrefsRepositoryImpl(
                dataStore = createTestPreferencesDataStore(backgroundScope, "reflection_prefs"),
                dispatchers = dispatchers,
            )

        assertEquals(ReflectionPrefs(), repository.get())
    }

    @Test
    fun update_persistsRoundTripAndObserves() = runDataTest { dispatchers ->
        val repository =
            ReflectionPrefsRepositoryImpl(
                dataStore = createTestPreferencesDataStore(backgroundScope, "reflection_prefs"),
                dispatchers = dispatchers,
            )
        val updated =
            ReflectionPrefs(
                consentAccepted = true,
                featureEnabled = true,
                windowStartMinuteOfDay = 18 * 60,
                windowEndMinuteOfDay = 21 * 60,
                reflectionFolder = "journal/reflections",
                modelOverride = "anthropic/claude-3.5-sonnet",
                usedFallbackPathNoticeShown = true,
            )

        repository.update { updated }

        assertEquals(updated, repository.get())
        assertEquals(updated, repository.observe().first())
    }

    @Test
    fun update_clearsModelOverrideWhenNull() = runDataTest { dispatchers ->
        val repository =
            ReflectionPrefsRepositoryImpl(
                dataStore = createTestPreferencesDataStore(backgroundScope, "reflection_prefs"),
                dispatchers = dispatchers,
            )

        repository.update { it.copy(modelOverride = "openai/gpt-4o-mini") }
        repository.update { it.copy(modelOverride = null) }

        assertNull(repository.get().modelOverride)
    }

    @Test
    fun update_partialTransformPreservesOtherFields() = runDataTest { dispatchers ->
        val repository =
            ReflectionPrefsRepositoryImpl(
                dataStore = createTestPreferencesDataStore(backgroundScope, "reflection_prefs"),
                dispatchers = dispatchers,
            )

        repository.update {
            it.copy(
                consentAccepted = true,
                reflectionFolder = "notes/reflections",
            )
        }
        repository.update { it.copy(featureEnabled = true) }

        val prefs = repository.get()
        assertEquals(true, prefs.consentAccepted)
        assertEquals(true, prefs.featureEnabled)
        assertEquals("notes/reflections", prefs.reflectionFolder)
        assertEquals(ReflectionConstants.DEFAULT_WINDOW_START_MINUTE, prefs.windowStartMinuteOfDay)
    }
}
