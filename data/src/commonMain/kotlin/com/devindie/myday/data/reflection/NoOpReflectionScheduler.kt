package com.devindie.myday.data.reflection

import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import com.devindie.myday.domain.repository.ReflectionSchedulerPort

/**
 * Placeholder until Task 11 wires platform background scheduling.
 *
 * TODO(Task 11): replace with Android WorkManager / iOS BGTaskScheduler implementation.
 */
class NoOpReflectionScheduler : ReflectionSchedulerPort {
    override fun reschedule(prefs: ReflectionPrefs) = Unit

    override fun cancel() = Unit
}
