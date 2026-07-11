package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.repository.AiKeyRepository

class SetOpenRouterKeyUseCase(private val keys: AiKeyRepository) {
    suspend operator fun invoke(key: String) {
        keys.setOpenRouterKey(key)
    }
}
