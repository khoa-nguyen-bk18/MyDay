package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.model.reflection.Draft
import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.model.reflection.ReflectionConstants
import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.domain.repository.AiKeyRepository
import com.devindie.myday.domain.repository.DraftRepository
import com.devindie.myday.domain.repository.ReflectionPrefsRepository
import com.devindie.myday.domain.repository.ReflectionRepository

class ShortenReflectionDraftUseCase(
    private val drafts: DraftRepository,
    private val keys: AiKeyRepository,
    private val prefs: ReflectionPrefsRepository,
    private val reflections: ReflectionRepository,
    private val todayIso: () -> IsoDate,
) {
    suspend operator fun invoke(): Result<Draft> = runCatching {
        val draft = drafts.get(todayIso()) ?: throw ReflectionError.DraftMissing
        val key = keys.getOpenRouterKey() ?: throw ReflectionError.KeyMissing
        val p = prefs.get()
        val model =
            p.modelOverride?.takeIf { it.isNotBlank() }
                ?: ReflectionConstants.DEFAULT_OPENROUTER_MODEL
        val shortened = reflections.shortenMarkdown(draft.markdown, model, key).getOrThrow()
        val updated = draft.copy(markdown = shortened)
        drafts.save(updated)
        updated
    }
}
