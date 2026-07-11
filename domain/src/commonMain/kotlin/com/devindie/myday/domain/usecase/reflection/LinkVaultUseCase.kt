package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.model.reflection.VaultLink
import com.devindie.myday.domain.repository.DailyNoteRepository

class LinkVaultUseCase(private val dailyNotes: DailyNoteRepository) {
    suspend operator fun invoke(tokenValue: String) {
        dailyNotes.setVaultLink(VaultLink(tokenValue))
    }
}
