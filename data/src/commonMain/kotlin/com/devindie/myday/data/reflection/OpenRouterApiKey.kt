package com.devindie.myday.data.reflection

import kotlinx.serialization.Serializable

/** OpenRouter API key persisted as one encrypted JSON blob in [KSafeOpenRouterKeyStore]. */
@Serializable
internal data class OpenRouterApiKey(val value: String = "")
