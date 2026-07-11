package com.devindie.myday.domain.repository

import com.devindie.myday.domain.model.reflection.ReflectionPrefs

interface ReflectionSchedulerPort {
    fun reschedule(prefs: ReflectionPrefs)

    fun cancel()
}
