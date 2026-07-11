package com.devindie.myday.domain.repository

import com.devindie.myday.domain.model.reflection.Draft
import com.devindie.myday.domain.model.reflection.IsoDate

interface DraftRepository {
    suspend fun get(date: IsoDate): Draft?

    suspend fun save(draft: Draft)

    suspend fun clear(date: IsoDate)
}
