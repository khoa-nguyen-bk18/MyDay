package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.fake.FakeDailyNoteRepository
import com.devindie.myday.domain.model.reflection.VaultLink
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LinkVaultUseCaseTest {
    @Test
    fun invoke_persistsVaultLink() = runTest {
        val notes = FakeDailyNoteRepository(vaultLink = null)
        val uc = LinkVaultUseCase(notes)

        uc("tree-token-123")

        assertEquals(VaultLink("tree-token-123"), notes.lastVaultLinkSet)
        assertEquals(VaultLink("tree-token-123"), notes.getVaultLink())
    }
}
