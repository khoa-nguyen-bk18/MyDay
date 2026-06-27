package com.devindie.myday.domain.usecase.user

import com.devindie.myday.domain.model.user.UserSession
import com.devindie.myday.domain.repository.UserRepository
import com.devindie.myday.domain.usecase.UseCaseNoParams

/**
 * Reads the current authenticated session, if any.
 *
 * @see UserRepository.getSession
 */
class GetUserSessionUseCase(private val repository: UserRepository) : UseCaseNoParams<UserSession?> {
    override suspend fun invoke(): UserSession? = repository.getSession()
}
