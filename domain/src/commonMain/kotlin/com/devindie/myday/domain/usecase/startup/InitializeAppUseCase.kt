package com.devindie.myday.domain.usecase.startup

import com.devindie.myday.domain.repository.AppStartupRepository
import com.devindie.myday.domain.usecase.UseCaseNoParams

class InitializeAppUseCase(private val repository: AppStartupRepository) : UseCaseNoParams<Result<Unit>> {
    override suspend fun invoke(): Result<Unit> = repository.ensureReady()
}
