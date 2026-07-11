package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.model.reflection.ReflectionSetupState
import com.devindie.myday.domain.repository.AiKeyRepository
import com.devindie.myday.domain.repository.DailyNoteRepository
import com.devindie.myday.domain.repository.ReflectionPrefsRepository

class GetReflectionSetupUseCase(
    private val prefs: ReflectionPrefsRepository,
    private val keys: AiKeyRepository,
    private val dailyNotes: DailyNoteRepository,
) {
    suspend operator fun invoke(): ReflectionSetupState = ReflectionSetupState(
        prefs = prefs.get(),
        hasOpenRouterKey = keys.getOpenRouterKey() != null,
        vaultLinked = dailyNotes.getVaultLink() != null,
    )
}
