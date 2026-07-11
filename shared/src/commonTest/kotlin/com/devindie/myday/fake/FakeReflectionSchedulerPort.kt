package com.devindie.myday.fake

import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import com.devindie.myday.domain.repository.ReflectionSchedulerPort

class FakeReflectionSchedulerPort : ReflectionSchedulerPort {
    var rescheduleCount = 0
    var cancelCount = 0

    override fun reschedule(prefs: ReflectionPrefs) {
        rescheduleCount++
    }

    override fun cancel() {
        cancelCount++
    }
}
