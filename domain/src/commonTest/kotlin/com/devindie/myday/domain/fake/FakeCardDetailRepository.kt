package com.devindie.myday.domain.fake

import com.devindie.myday.domain.model.carddetail.CardDetail
import com.devindie.myday.domain.repository.CardDetailRepository

class FakeCardDetailRepository : CardDetailRepository {
    var getCardDetailResult: Result<CardDetail>? = null
    var lastRequestedCardId: Long? = null

    override suspend fun getCardDetail(cardId: Long): Result<CardDetail> {
        lastRequestedCardId = cardId
        return getCardDetailResult ?: Result.failure(IllegalStateException("No stubbed result"))
    }
}
