package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import com.devindie.myday.domain.repository.ReflectionPrefsRepository
import com.devindie.myday.domain.repository.ReflectionSchedulerPort

class UpdateReflectionPrefsUseCase(
    private val prefs: ReflectionPrefsRepository,
    private val scheduler: ReflectionSchedulerPort,
) {
    suspend operator fun invoke(transform: (ReflectionPrefs) -> ReflectionPrefs) {
        prefs.update(transform)
        val updated = prefs.get()
        if (updated.featureEnabled) {
            scheduler.reschedule(updated)
        } else {
            scheduler.cancel()
        }
    }
}
