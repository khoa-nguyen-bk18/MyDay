package com.devindie.myday.domain.usecase.reflection

fun isWithinWindow(minuteOfDay: Int, start: Int, end: Int): Boolean = if (start <= end) {
    minuteOfDay in start until end
} else {
    minuteOfDay >= start || minuteOfDay < end
}
