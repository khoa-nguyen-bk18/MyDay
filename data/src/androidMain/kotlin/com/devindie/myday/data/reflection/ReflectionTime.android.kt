package com.devindie.myday.data.reflection

import com.devindie.myday.domain.model.reflection.IsoDate
import java.time.LocalDate
import java.time.LocalTime

private const val MINUTES_PER_HOUR = 60

internal actual fun reflectionEpochMillis(): Long = System.currentTimeMillis()

internal actual fun reflectionTodayIso(): IsoDate = LocalDate.now().toString()

internal actual fun reflectionMinuteOfDay(): Int {
    val now = LocalTime.now()
    return now.hour * MINUTES_PER_HOUR + now.minute
}
