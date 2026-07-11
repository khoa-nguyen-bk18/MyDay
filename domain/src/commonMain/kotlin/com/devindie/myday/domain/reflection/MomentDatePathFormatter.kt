package com.devindie.myday.domain.reflection

object MomentDatePathFormatter {
    private val leftoverTokenPattern = Regex("""[YyMdHhmsaA]{2,}""")

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
        val result =
            listOf(
                "YYYY" to year.toString().padStart(4, '0'),
                "MM" to month.toString().padStart(2, '0'),
                "DD" to day.toString().padStart(2, '0'),
                "HH" to hour.toString().padStart(2, '0'),
                "mm" to minute.toString().padStart(2, '0'),
            ).fold(format) { acc, (token, value) -> acc.replace(token, value) }
        return when {
            leftoverTokenPattern.containsMatchIn(result) -> null
            else -> {
                val file = if (result.endsWith(".md")) result else "$result.md"
                val prefix = folder.trim('/').trim()
                if (prefix.isEmpty()) file else "$prefix/$file"
            }
        }
    }
}
