package com.devindie.myday.domain.fake

import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import com.devindie.myday.domain.repository.ReflectionSchedulerPort

class FakeReflectionSchedulerPort : ReflectionSchedulerPort {
    var rescheduleCallCount: Int = 0
    var cancelCallCount: Int = 0
    var lastReschedulePrefs: ReflectionPrefs? = null

    override fun reschedule(prefs: ReflectionPrefs) {
        rescheduleCallCount++
        lastReschedulePrefs = prefs
    }

    override fun cancel() {
        cancelCallCount++
    }
}
