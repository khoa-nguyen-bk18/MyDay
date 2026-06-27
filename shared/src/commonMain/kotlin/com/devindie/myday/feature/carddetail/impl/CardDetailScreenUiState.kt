package com.devindie.myday.feature.carddetail.impl

import com.devindie.myday.domain.model.carddetail.CardCondition
import com.devindie.myday.domain.model.carddetail.CardDetail

data class CardDetailScreenUiState(
    val isLoading: Boolean = true,
    val card: CardDetail? = null,
    val selectedCondition: CardCondition = CardCondition.NearMint,
    val selectedPriceDisplay: String = "",
    val errorMessage: String? = null,
)
