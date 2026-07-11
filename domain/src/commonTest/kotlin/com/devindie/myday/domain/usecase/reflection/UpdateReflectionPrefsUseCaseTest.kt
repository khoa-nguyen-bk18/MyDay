package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.fake.FakeReflectionPrefsRepository
import com.devindie.myday.domain.fake.FakeReflectionSchedulerPort
import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateReflectionPrefsUseCaseTest {
    @Test
    fun featureEnabled_reschedulesScheduler() = runTest {
        val prefs = FakeReflectionPrefsRepository(ReflectionPrefs(featureEnabled = false))
        val scheduler = FakeReflectionSchedulerPort()
        val uc = UpdateReflectionPrefsUseCase(prefs, scheduler)

        uc { it.copy(featureEnabled = true, consentAccepted = true) }

        assertEquals(1, scheduler.rescheduleCallCount)
        assertEquals(0, scheduler.cancelCallCount)
        assertEquals(true, scheduler.lastReschedulePrefs?.featureEnabled)
    }

    @Test
    fun featureDisabled_cancelsScheduler() = runTest {
        val prefs = FakeReflectionPrefsRepository(ReflectionPrefs(featureEnabled = true))
        val scheduler = FakeReflectionSchedulerPort()
        val uc = UpdateReflectionPrefsUseCase(prefs, scheduler)

        uc { it.copy(featureEnabled = false) }

        assertEquals(0, scheduler.rescheduleCallCount)
        assertEquals(1, scheduler.cancelCallCount)
    }
}
