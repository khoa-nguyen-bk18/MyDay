package com.devindie.myday.data.reflection

import com.devindie.myday.domain.model.reflection.IsoDate
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.date
import platform.Foundation.timeIntervalSince1970

private const val MINUTES_PER_HOUR = 60
private const val MILLIS_PER_SECOND = 1_000

@OptIn(ExperimentalForeignApi::class)
internal actual fun reflectionEpochMillis(): Long = (NSDate().timeIntervalSince1970 * MILLIS_PER_SECOND).toLong()

@OptIn(ExperimentalForeignApi::class)
internal actual fun reflectionTodayIso(): IsoDate {
    val formatter = NSDateFormatter()
    formatter.locale = NSLocale.currentLocale
    formatter.dateFormat = "yyyy-MM-dd"
    return formatter.stringFromDate(NSDate.date())
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun reflectionMinuteOfDay(): Int {
    val calendar = NSCalendar.currentCalendar
    val components =
        calendar.components(
            unitFlags = platform.Foundation.NSCalendarUnitHour or platform.Foundation.NSCalendarUnitMinute,
            fromDate = NSDate.date(),
        )
    return components.hour.toInt() * MINUTES_PER_HOUR + components.minute.toInt()
}
