package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.model.reflection.TodayDraft
import com.devindie.myday.domain.repository.DailyNoteRepository
import com.devindie.myday.domain.repository.DraftRepository

class GetTodayDraftUseCase(
    private val drafts: DraftRepository,
    private val dailyNotes: DailyNoteRepository,
    private val todayIso: () -> IsoDate,
) {
    suspend operator fun invoke(): TodayDraft {
        val date = todayIso()
        val draft = drafts.get(date) ?: return TodayDraft(draft = null, sourceChanged = false)
        val noteHash = dailyNotes.resolveAndRead(date).getOrNull()?.contentHash
        val sourceChanged = noteHash != null && noteHash != draft.sourceContentHash
        return TodayDraft(draft = draft, sourceChanged = sourceChanged)
    }
}
