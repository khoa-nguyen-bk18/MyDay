package com.devindie.myday.domain.fake

import com.devindie.myday.domain.model.reflection.DailyNoteContent
import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.model.reflection.VaultLink
import com.devindie.myday.domain.repository.DailyNoteRepository

class FakeDailyNoteRepository(
    private var vaultLink: VaultLink? = VaultLink("vault-token"),
    private var content: DailyNoteContent? = null,
    var resolveResult: Result<DailyNoteContent?>? = null,
) : DailyNoteRepository {
    var lastVaultLinkSet: VaultLink? = null

    override suspend fun getVaultLink(): VaultLink? = vaultLink

    override suspend fun setVaultLink(link: VaultLink) {
        vaultLink = link
        lastVaultLinkSet = link
    }

    override suspend fun clearVaultLink() {
        vaultLink = null
    }

    override suspend fun resolveAndRead(date: IsoDate): Result<DailyNoteContent?> =
        resolveResult ?: Result.success(content)

    fun setContent(value: DailyNoteContent?) {
        content = value
    }
}
