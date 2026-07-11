package com.devindie.myday.data.reflection

import com.devindie.myday.data.coroutines.DispatcherProvider
import com.devindie.myday.domain.model.reflection.Draft
import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.repository.DraftRepository
import kotlinx.coroutines.withContext

class DraftRepositoryImpl(
    private val localDataSource: DraftLocalDataSource,
    private val dispatchers: DispatcherProvider,
) : DraftRepository {
    override suspend fun get(date: IsoDate): Draft? = withContext(dispatchers.io) {
        localDataSource.get(date)
    }

    override suspend fun save(draft: Draft) {
        withContext(dispatchers.io) {
            localDataSource.save(draft)
        }
    }

    override suspend fun clear(date: IsoDate) {
        withContext(dispatchers.io) {
            localDataSource.clear(date)
        }
    }
}
