package com.devindie.myday.domain.usecase.user

import com.devindie.myday.domain.fake.FakeUserRepository
import com.devindie.myday.domain.model.user.UserSession
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetUserSessionUseCaseTest {
    @Test
    fun invoke_returnsNullWhenLoggedOut() = runTest {
        val repository = FakeUserRepository()
        val useCase = GetUserSessionUseCase(repository)

        assertNull(useCase())
    }

    @Test
    fun invoke_returnsSessionFromRepository() = runTest {
        val session = UserSession(accessToken = "access", refreshToken = "refresh")
        val repository =
            FakeUserRepository().apply {
                this.session = session
            }
        val useCase = GetUserSessionUseCase(repository)

        assertEquals(session, useCase())
    }
}
