package com.devindie.myday.data.reflection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences

fun createReflectionPrefsDataStore(context: Context): DataStore<Preferences> = PreferenceDataStoreFactory.create(
    produceFile = { context.filesDir.resolve(REFLECTION_PREFS_DATASTORE_FILE) },
)
