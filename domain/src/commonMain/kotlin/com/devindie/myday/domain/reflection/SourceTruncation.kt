package com.devindie.myday.domain.reflection

import com.devindie.myday.domain.model.reflection.ReflectionConstants

data class TruncatedSource(val text: String, val truncated: Boolean)

object SourceTruncation {
    fun fromEnd(text: String, maxChars: Int = ReflectionConstants.MAX_SOURCE_CHARS): TruncatedSource {
        if (text.length <= maxChars) return TruncatedSource(text, truncated = false)
        return TruncatedSource(text.takeLast(maxChars), truncated = true)
    }
}
