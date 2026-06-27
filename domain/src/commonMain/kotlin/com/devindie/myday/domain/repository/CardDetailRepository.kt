package com.devindie.myday.domain.repository

import com.devindie.myday.domain.model.carddetail.CardDetail

interface CardDetailRepository {
    suspend fun getCardDetail(cardId: Long): Result<CardDetail>
}
