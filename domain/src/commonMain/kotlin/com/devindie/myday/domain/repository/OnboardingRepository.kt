package com.devindie.myday.domain.repository

interface OnboardingRepository {
    suspend fun hasCompleted(): Boolean

    suspend fun markCompleted()
}
