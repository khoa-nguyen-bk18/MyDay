package com.devindie.myday.data.source.local.browse

import com.devindie.myday.data.coroutines.DispatcherProvider
import com.devindie.myday.data.coroutines.runIoResult
import com.devindie.myday.domain.model.carddetail.CardDetail
import com.devindie.myday.domain.repository.CardDetailRepository
import kotlinx.coroutines.withContext

class CardDetailRepositoryImpl(
    private val localDataSource: BrowseCardLocalDataSource,
    private val dispatchers: DispatcherProvider,
) : CardDetailRepository {
    override suspend fun getCardDetail(cardId: Long): Result<CardDetail> = withContext(dispatchers.io) {
        runIoResult {
            localDataSource.getCardDetail(cardId)
                ?: error("Card not found")
        }
    }
}
