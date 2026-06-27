package com.devindie.myday.data.source.startup

import com.devindie.myday.data.source.local.browse.BrowseCardDao
import com.devindie.myday.domain.repository.AppStartupRepository

class AppStartupRepositoryImpl(private val browseCardDao: BrowseCardDao) : AppStartupRepository {
    override suspend fun ensureReady(): Result<Unit> = runCatching { browseCardDao.count() }
}
