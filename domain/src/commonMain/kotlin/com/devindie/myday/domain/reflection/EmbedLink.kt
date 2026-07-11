package com.devindie.myday.domain.reflection

object EmbedLink {
    fun wikiPath(folder: String, dateIso: String): String = "${folder.trim('/').trim()}/$dateIso"

    fun build(folder: String, dateIso: String): String = "![[${wikiPath(folder, dateIso)}]]"

    fun contains(dailyNoteBody: String, folder: String, dateIso: String): Boolean {
        val target = wikiPath(folder, dateIso)
        return dailyNoteBody.contains("![[$target]]") ||
            dailyNoteBody.contains("![[$target.md]]")
    }

    fun appendBlock(dailyNoteBody: String, folder: String, dateIso: String): String {
        if (contains(dailyNoteBody, folder, dateIso)) return dailyNoteBody
        val block = "\n\n## Daily Reflection\n\n${build(folder, dateIso)}\n"
        return dailyNoteBody.trimEnd() + block
    }
}
