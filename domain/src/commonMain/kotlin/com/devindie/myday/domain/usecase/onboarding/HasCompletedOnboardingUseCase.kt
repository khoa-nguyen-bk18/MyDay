package com.devindie.myday.domain.usecase.onboarding

import com.devindie.myday.domain.repository.OnboardingRepository
import com.devindie.myday.domain.usecase.UseCaseNoParams

class HasCompletedOnboardingUseCase(private val repository: OnboardingRepository) : UseCaseNoParams<Boolean> {
    override suspend fun invoke(): Boolean = repository.hasCompleted()
}
