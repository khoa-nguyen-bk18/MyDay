package com.devindie.myday.data.reflection

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import okio.FileSystem
import kotlin.random.Random

internal fun createTestPreferencesDataStore(scope: CoroutineScope, filePrefix: String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        scope = scope,
        produceFile = {
            FileSystem.SYSTEM_TEMPORARY_DIRECTORY /
                "${filePrefix}_${Random.nextLong()}.preferences_pb"
        },
    )
