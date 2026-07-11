package com.devindie.myday.data.reflection

import com.devindie.myday.domain.model.reflection.DailyNoteRef
import com.devindie.myday.domain.model.reflection.DailyNoteResolution
import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.reflection.MomentDatePathFormatter

internal const val PERIODIC_NOTES_CONFIG_PATH = ".obsidian/plugins/periodic-notes/data.json"
internal const val CORE_DAILY_NOTES_CONFIG_PATH = ".obsidian/daily-notes.json"

private const val ISO_DATE_PARTS = 3

class ObsidianDailyNoteResolver(private val reader: VaultFileReader) {
    @Suppress("ReturnCount")
    suspend fun resolve(date: IsoDate): DailyNoteRef {
        val (year, month, day) = parseIsoDate(date)
        resolvePeriodic(date, year, month, day)?.let { return it }
        resolveCore(date, year, month, day)?.let { return it }
        return DailyNoteRef(
            date = date,
            relativePath = "$date.md",
            resolution = DailyNoteResolution.Fallback,
        )
    }

    @Suppress("ReturnCount")
    private suspend fun resolvePeriodic(date: IsoDate, year: Int, month: Int, day: Int): DailyNoteRef? {
        val raw = reader.readTextOrNull(PERIODIC_NOTES_CONFIG_PATH) ?: return null
        val config = parsePeriodicNotesConfig(raw) ?: return null
        if (!config.enabled) return null
        return pathRef(date, config.folder, config.format, year, month, day, DailyNoteResolution.PeriodicNotes)
    }

    @Suppress("ReturnCount")
    private suspend fun resolveCore(date: IsoDate, year: Int, month: Int, day: Int): DailyNoteRef? {
        val raw = reader.readTextOrNull(CORE_DAILY_NOTES_CONFIG_PATH) ?: return null
        val config = parseObsidianDailyNotesConfig(raw) ?: return null
        return pathRef(date, config.folder, config.format, year, month, day, DailyNoteResolution.CoreDailyNotes)
    }

    private fun pathRef(
        date: IsoDate,
        folder: String,
        format: String,
        year: Int,
        month: Int,
        day: Int,
        resolution: DailyNoteResolution,
    ): DailyNoteRef? {
        val relativePath =
            MomentDatePathFormatter.format(
                folder = folder,
                format = format,
                year = year,
                month = month,
                day = day,
            ) ?: return null
        return DailyNoteRef(date = date, relativePath = relativePath, resolution = resolution)
    }

    private fun parseIsoDate(date: IsoDate): Triple<Int, Int, Int> {
        val parts = date.split("-")
        require(parts.size == ISO_DATE_PARTS) { "Invalid IsoDate: $date" }
        return Triple(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
    }
}
