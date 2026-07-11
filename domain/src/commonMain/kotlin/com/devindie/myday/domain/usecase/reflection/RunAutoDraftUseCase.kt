package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.repository.DraftRepository
import com.devindie.myday.domain.repository.ReflectionPrefsRepository

class RunAutoDraftUseCase(
    private val drafts: DraftRepository,
    private val prefs: ReflectionPrefsRepository,
    private val generate: GenerateReflectionDraftUseCase,
    private val minuteOfDay: () -> Int,
    private val todayIso: () -> IsoDate,
) {
    suspend operator fun invoke(): AutoDraftResult {
        val p = prefs.get()
        return when {
            !isWithinWindow(minuteOfDay(), p.windowStartMinuteOfDay, p.windowEndMinuteOfDay) ->
                AutoDraftResult.SkippedOutsideWindow
            drafts.get(todayIso()) != null -> AutoDraftResult.SkippedDraftExists
            else ->
                generate().fold(
                    onSuccess = { AutoDraftResult.Generated(it) },
                    onFailure = { throw it },
                )
        }
    }
}
