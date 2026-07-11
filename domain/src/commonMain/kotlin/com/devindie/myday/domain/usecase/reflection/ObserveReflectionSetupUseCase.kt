package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.model.reflection.ReflectionSetupState
import com.devindie.myday.domain.repository.AiKeyRepository
import com.devindie.myday.domain.repository.DailyNoteRepository
import com.devindie.myday.domain.repository.ReflectionPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ObserveReflectionSetupUseCase(
    private val prefs: ReflectionPrefsRepository,
    private val keys: AiKeyRepository,
    private val dailyNotes: DailyNoteRepository,
) {
    operator fun invoke(): Flow<ReflectionSetupState> = flow {
        prefs.observe().collect { currentPrefs ->
            emit(
                ReflectionSetupState(
                    prefs = currentPrefs,
                    hasOpenRouterKey = keys.getOpenRouterKey() != null,
                    vaultLinked = dailyNotes.getVaultLink() != null,
                ),
            )
        }
    }
}
