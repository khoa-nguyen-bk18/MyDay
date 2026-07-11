package com.devindie.myday.domain.fake

import com.devindie.myday.domain.model.reflection.Draft
import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.repository.DraftRepository

class FakeDraftRepository : DraftRepository {
    private val drafts = mutableMapOf<IsoDate, Draft>()

    override suspend fun get(date: IsoDate): Draft? = drafts[date]

    override suspend fun save(draft: Draft) {
        drafts[draft.date] = draft
    }

    override suspend fun clear(date: IsoDate) {
        drafts.remove(date)
    }
}
