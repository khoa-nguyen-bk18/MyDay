package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.model.reflection.ReflectionDocument
import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.domain.repository.DraftRepository
import com.devindie.myday.domain.repository.ReflectionPrefsRepository
import com.devindie.myday.domain.repository.ReflectionRepository

class SaveReflectionUseCase(
    private val drafts: DraftRepository,
    private val prefs: ReflectionPrefsRepository,
    private val reflections: ReflectionRepository,
    private val todayIso: () -> IsoDate,
) {
    suspend operator fun invoke(replaceExisting: Boolean): Result<ReflectionDocument> = runCatching {
        val draft = drafts.get(todayIso()) ?: throw ReflectionError.DraftMissing
        val p = prefs.get()
        val exists =
            reflections.reflectionFileExists(draft.date, p.reflectionFolder).getOrThrow()
        if (exists && !replaceExisting) throw ReflectionError.AlreadyExists
        reflections.saveToVault(
            date = draft.date,
            folder = p.reflectionFolder,
            markdown = draft.markdown,
            replaceExistingFile = replaceExisting,
        ).getOrThrow()
    }
}
