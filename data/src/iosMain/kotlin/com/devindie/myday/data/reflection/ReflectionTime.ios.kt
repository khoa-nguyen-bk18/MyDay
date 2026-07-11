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

@OptIn(ExperimentalForeignApi::class)
internal actual fun reflectionEpochMillis(): Long = (NSDate().timeIntervalSince1970 * 1_000).toLong()

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
    val components = calendar.components(
        unitFlags = platform.Foundation.NSCalendarUnitHour or platform.Foundation.NSCalendarUnitMinute,
        fromDate = NSDate.date(),
    )
    return components.hour.toInt() * 60 + components.minute.toInt()
}
