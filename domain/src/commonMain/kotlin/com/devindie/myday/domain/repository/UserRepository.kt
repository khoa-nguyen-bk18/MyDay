package com.devindie.myday.domain.repository

import com.devindie.myday.domain.model.user.UserSession

/**
 * Persists and reads the authenticated user session (OAuth tokens).
 *
 * **Flow:** use cases in `domain.usecase.user` → this → [com.devindie.myday.data.auth.UserRepositoryImpl].
 *
 * @see com.devindie.myday.domain.usecase.user.GetUserSessionUseCase
 * @see com.devindie.myday.domain.usecase.user.SaveUserSessionUseCase
 * @see com.devindie.myday.domain.usecase.user.ClearUserSessionUseCase
 */
interface UserRepository {
    /** Returns a session when both tokens are present; `null` when logged out. */
    suspend fun getSession(): UserSession?

    suspend fun saveSession(session: UserSession)

    suspend fun clearSession()
}
