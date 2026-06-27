package com.devindie.myday.domain.usecase.onboarding

import com.devindie.myday.domain.repository.OnboardingRepository
import com.devindie.myday.domain.usecase.UseCaseNoParams

class CompleteOnboardingUseCase(private val repository: OnboardingRepository) : UseCaseNoParams<Unit> {
    override suspend fun invoke() {
        repository.markCompleted()
    }
}
