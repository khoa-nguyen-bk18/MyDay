package com.devindie.myday.data.reflection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences

fun createDraftDataStore(context: Context): DataStore<Preferences> = PreferenceDataStoreFactory.create(
    produceFile = { context.filesDir.resolve(DRAFT_DATASTORE_FILE) },
)
