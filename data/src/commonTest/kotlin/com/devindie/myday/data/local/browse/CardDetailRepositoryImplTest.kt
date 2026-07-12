package com.devindie.myday.data.local.browse

import com.devindie.myday.data.coroutines.runDataTest
import com.devindie.myday.data.local.browse.fake.FakeBrowseCardDao
import com.devindie.myday.data.source.local.browse.BrowseCardLocalDataSourceImpl
import com.devindie.myday.data.source.local.browse.CardDetailRepositoryImpl
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CardDetailRepositoryImplTest {
    @Test
    fun getCardDetail_returnsMappedDetail() = runDataTest { dispatchers ->
        val dao = FakeBrowseCardDao()
        dao.setCards(listOf(sampleBrowseCardEntity(id = 3L)))
        val repository =
            CardDetailRepositoryImpl(
                localDataSource = BrowseCardLocalDataSourceImpl(dao),
                dispatchers = dispatchers,
            )

        val result = repository.getCardDetail(3L)
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        assertEquals(3L, result.getOrNull()?.id)
    }

    @Test
    fun getCardDetail_returnsFailureWhenMissing() = runDataTest { dispatchers ->
        val repository =
            CardDetailRepositoryImpl(
                localDataSource = BrowseCardLocalDataSourceImpl(FakeBrowseCardDao()),
                dispatchers = dispatchers,
            )

        val result = repository.getCardDetail(99L)
        advanceUntilIdle()

        assertTrue(result.isFailure)
    }
}
