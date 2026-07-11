package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.repository.ReflectionPrefsRepository
import com.devindie.myday.domain.repository.ReflectionSchedulerPort

/** Re-applies WorkManager / iOS schedule from persisted prefs (app cold start). */
class EnsureReflectionScheduleUseCase(
    private val prefs: ReflectionPrefsRepository,
    private val scheduler: ReflectionSchedulerPort,
) {
    suspend operator fun invoke() {
        val updated = prefs.get()
        if (updated.featureEnabled) {
            scheduler.reschedule(updated)
        } else {
            scheduler.cancel()
        }
    }
}
