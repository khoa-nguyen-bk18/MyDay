package com.devindie.myday.domain.usecase.user

import com.devindie.myday.domain.repository.UserRepository
import com.devindie.myday.domain.usecase.UseCaseNoParams

/**
 * Clears stored tokens (logout).
 *
 * @see UserRepository.clearSession
 */
class ClearUserSessionUseCase(private val repository: UserRepository) : UseCaseNoParams<Unit> {
    override suspend fun invoke() {
        repository.clearSession()
    }
}
