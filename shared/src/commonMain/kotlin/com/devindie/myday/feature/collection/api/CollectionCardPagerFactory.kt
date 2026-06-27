package com.devindie.myday.feature.collection.api

import androidx.paging.PagingData
import com.devindie.myday.domain.model.browse.BrowseCardsQuery
import com.devindie.myday.domain.model.browse.CollectibleCard
import kotlinx.coroutines.flow.Flow

/**
 * Presentation port for paginated Browse catalog streams.
 *
 * Implementation lives in `:data` ([com.devindie.myday.data.source.local.browse.BrowseCardPagerFactoryImpl]);
 * bound at the app composition root (androidApp / iOS Koin bootstrap), not in
 * [com.devindie.myday.core.di.appDomainModule].
 */
fun interface CollectionCardPagerFactory {
    fun pages(query: BrowseCardsQuery): Flow<PagingData<CollectibleCard>>
}
