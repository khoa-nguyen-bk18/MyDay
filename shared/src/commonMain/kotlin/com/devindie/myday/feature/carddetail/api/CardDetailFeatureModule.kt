package com.devindie.myday.feature.carddetail.api

import com.devindie.myday.feature.carddetail.impl.CardDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val cardDetailFeatureModule =
    module {
        viewModel { (cardId: Long) ->
            CardDetailViewModel(
                getCardDetail = get(),
                cardId = cardId,
            )
        }
    }
