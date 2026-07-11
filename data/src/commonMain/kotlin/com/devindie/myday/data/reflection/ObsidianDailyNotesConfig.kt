package com.devindie.myday.data.reflection

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class ObsidianDailyNotesConfig(val format: String = "", val folder: String = "")

private val obsidianConfigJson = Json { ignoreUnknownKeys = true }

internal fun parseObsidianDailyNotesConfig(raw: String): ObsidianDailyNotesConfig? = runCatching {
    obsidianConfigJson.decodeFromString(ObsidianDailyNotesConfig.serializer(), raw)
}.getOrNull()
