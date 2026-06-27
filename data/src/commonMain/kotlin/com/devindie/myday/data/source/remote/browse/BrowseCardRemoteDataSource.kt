package com.devindie.myday.data.source.remote.browse

import com.devindie.myday.data.network.ApiResult
import com.devindie.myday.data.network.dto.BrowseCatalogPageDto

/**
 * Remote catalog port; implementations live in this module (fake or Ktor).
 *
 * @see FakeBrowseCardRemoteDataSource
 * @see KtorBrowseCardRemoteDataSource
 * @see com.devindie.myday.data.source.local.browse.BrowseCardRemoteMediator
 */
interface BrowseCardRemoteDataSource {
    suspend fun fetchCatalogPage(page: Int, pageSize: Int): ApiResult<BrowseCatalogPageDto>
}
