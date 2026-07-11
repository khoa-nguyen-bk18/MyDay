package com.devindie.myday.data.reflection

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class PeriodicNotesConfigRoot(val daily: PeriodicNotesDailyConfig? = null)

@Serializable
internal data class PeriodicNotesDailyConfig(
    val format: String = "",
    val folder: String = "",
    val enabled: Boolean = true,
)

private val periodicNotesJson = Json { ignoreUnknownKeys = true }

internal fun parsePeriodicNotesConfig(raw: String): PeriodicNotesDailyConfig? = runCatching {
    periodicNotesJson.decodeFromString(PeriodicNotesConfigRoot.serializer(), raw).daily
}.getOrNull()
