package com.devindie.myday.domain.model.user

/**
 * Authenticated session backed by OAuth-style access and refresh tokens.
 *
 * @see com.devindie.myday.domain.repository.UserRepository
 */
data class UserSession(val accessToken: String, val refreshToken: String)
