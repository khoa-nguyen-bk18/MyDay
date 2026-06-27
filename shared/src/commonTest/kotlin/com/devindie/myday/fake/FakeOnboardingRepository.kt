package com.devindie.myday.fake

import com.devindie.myday.domain.repository.OnboardingRepository

class FakeOnboardingRepository(private var completed: Boolean = false) : OnboardingRepository {
    var markCompletedCallCount: Int = 0

    override suspend fun hasCompleted(): Boolean = completed

    override suspend fun markCompleted() {
        markCompletedCallCount++
        completed = true
    }
}
