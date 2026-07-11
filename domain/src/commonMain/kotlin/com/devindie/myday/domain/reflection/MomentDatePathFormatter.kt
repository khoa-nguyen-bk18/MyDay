package com.devindie.myday.domain.reflection

object MomentDatePathFormatter {
    /**
     * @return relative path including `.md`, or null if [format] has unsupported tokens.
     */
    fun format(
        folder: String,
        format: String,
        year: Int,
        month: Int,
        day: Int,
        hour: Int = 0,
        minute: Int = 0,
    ): String? {
        if (format.contains("dddd", ignoreCase = false) ||
            format.contains("MMMM") ||
            format.contains("ddd")
        ) {
            return null
        }
        var result = format
        val replacements =
            listOf(
                "YYYY" to year.toString().padStart(4, '0'),
                "MM" to month.toString().padStart(2, '0'),
                "DD" to day.toString().padStart(2, '0'),
                "HH" to hour.toString().padStart(2, '0'),
                "mm" to minute.toString().padStart(2, '0'),
            )
        for ((token, value) in replacements) {
            result = result.replace(token, value)
        }
        if (Regex("""[YyMdHhmsaA]{2,}""").containsMatchIn(result)) return null
        val file = if (result.endsWith(".md")) result else "$result.md"
        val prefix = folder.trim('/').trim()
        return if (prefix.isEmpty()) file else "$prefix/$file"
    }
}
