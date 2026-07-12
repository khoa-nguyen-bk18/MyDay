package com.devindie.myday.dev

import co.touchlab.kermit.Logger
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializerOrNull

private val logJson = Json {
    prettyPrint = true
    encodeDefaults = true
    ignoreUnknownKeys = true
}

/**
 * Dev-only log via [Kermit](https://kermit.touchlab.co/docs/). No-op when the current build is not
 * debuggable. Prefixes the current thread name; serializes non-[String] values when a kotlinx
 * serializer is available, otherwise falls back to [Any.toString].
 */
fun log(value: Any) {
    if (!isDevBuild()) return
    Logger.d { "[${currentThreadName()}] ${formatLogValue(value)}" }
}

@OptIn(InternalSerializationApi::class)
private fun formatLogValue(value: Any): String {
    if (value is String) return value
    val serializer = value::class.serializerOrNull() ?: return value.toString()
    @Suppress("UNCHECKED_CAST")
    return runCatching {
        logJson.encodeToString(serializer as KSerializer<Any>, value)
    }.getOrElse { value.toString() }
}

internal expect fun isDevBuild(): Boolean

internal expect fun currentThreadName(): String
