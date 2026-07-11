package com.devindie.myday.domain.reflection

import com.devindie.myday.domain.model.reflection.ReflectionConstants

object ContentSufficiency {
    private val headingOrEmptyTask =
        Regex("""^\s*(#{1,6}\s+.*|- \[[ xX]?\]\s*)$""")

    fun isSufficient(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.length < ReflectionConstants.MIN_SUFFICIENT_CHARS) return false
        val substantive =
            trimmed.lineSequence()
                .map { it.trimEnd() }
                .filter { it.isNotBlank() }
                .filterNot { headingOrEmptyTask.matches(it) }
                .joinToString("\n")
        return substantive.length >= ReflectionConstants.MIN_SUFFICIENT_CHARS
    }
}
