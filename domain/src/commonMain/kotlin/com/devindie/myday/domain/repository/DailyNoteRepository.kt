package com.devindie.myday.domain.repository

import com.devindie.myday.domain.model.reflection.DailyNoteContent
import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.model.reflection.VaultLink

interface DailyNoteRepository {
    suspend fun getVaultLink(): VaultLink?

    suspend fun setVaultLink(link: VaultLink)

    suspend fun clearVaultLink()

    /** @return null if file does not exist after resolution */
    suspend fun resolveAndRead(date: IsoDate): Result<DailyNoteContent?>
}
