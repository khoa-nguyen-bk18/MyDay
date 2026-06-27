package com.devindie.myday.domain.usecase.carddetail

import com.devindie.myday.domain.model.carddetail.CardDetail
import com.devindie.myday.domain.repository.CardDetailRepository

class GetCardDetailUseCase(private val repository: CardDetailRepository) {
    suspend operator fun invoke(cardId: Long): Result<CardDetail> = repository.getCardDetail(cardId)
}
