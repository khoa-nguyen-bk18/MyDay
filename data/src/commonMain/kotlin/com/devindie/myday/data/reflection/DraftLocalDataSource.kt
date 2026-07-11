package com.devindie.myday.data.reflection

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.devindie.myday.domain.model.reflection.Draft
import com.devindie.myday.domain.model.reflection.IsoDate
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

private val DRAFTS_JSON_KEY = stringPreferencesKey("reflection_drafts_json")

class DraftLocalDataSource(private val dataStore: DataStore<Preferences>) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun get(date: IsoDate): Draft? = readPayload().drafts[date]?.toDomain()

    suspend fun save(draft: Draft) {
        dataStore.edit { preferences ->
            val payload = decodePayload(preferences[DRAFTS_JSON_KEY])
            preferences[DRAFTS_JSON_KEY] =
                json.encodeToString(
                    DraftStorePayload.serializer(),
                    payload.copy(drafts = payload.drafts + (draft.date to draft.toRecord())),
                )
        }
    }

    suspend fun clear(date: IsoDate) {
        dataStore.edit { preferences ->
            val payload = decodePayload(preferences[DRAFTS_JSON_KEY])
            if (date !in payload.drafts) return@edit
            preferences[DRAFTS_JSON_KEY] =
                json.encodeToString(
                    DraftStorePayload.serializer(),
                    payload.copy(drafts = payload.drafts - date),
                )
        }
    }

    private suspend fun readPayload(): DraftStorePayload = decodePayload(dataStore.data.first()[DRAFTS_JSON_KEY])

    private fun decodePayload(raw: String?): DraftStorePayload = raw?.let { encoded ->
        runCatching {
            json.decodeFromString(DraftStorePayload.serializer(), encoded)
        }.getOrDefault(DraftStorePayload())
    } ?: DraftStorePayload()
}
