package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.repository.AiKeyRepository

class ClearOpenRouterKeyUseCase(private val keys: AiKeyRepository) {
    suspend operator fun invoke() {
        keys.clearOpenRouterKey()
    }
}
