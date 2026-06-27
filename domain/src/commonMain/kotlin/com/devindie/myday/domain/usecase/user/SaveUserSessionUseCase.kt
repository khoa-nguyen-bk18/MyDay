package com.devindie.myday.domain.usecase.user

import com.devindie.myday.domain.model.user.UserSession
import com.devindie.myday.domain.repository.UserRepository
import com.devindie.myday.domain.usecase.UseCase

/**
 * Persists tokens after login or manual session update.
 *
 * @see UserRepository.saveSession
 */
class SaveUserSessionUseCase(private val repository: UserRepository) : UseCase<UserSession, Unit> {
    override suspend fun invoke(parameters: UserSession) {
        repository.saveSession(parameters)
    }
}
