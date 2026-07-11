package com.devindie.myday.data.reflection

import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import com.devindie.myday.domain.repository.ReflectionSchedulerPort

/** Test / fallback scheduler when platform WorkManager / iOS bindings are unavailable. */
class NoOpReflectionScheduler : ReflectionSchedulerPort {
    override fun reschedule(prefs: ReflectionPrefs) = Unit

    override fun cancel() = Unit
}
