package com.devindie.myday.data.reflection

import com.devindie.myday.domain.model.reflection.IsoDate

internal expect fun reflectionEpochMillis(): Long

internal expect fun reflectionTodayIso(): IsoDate

internal expect fun reflectionMinuteOfDay(): Int
