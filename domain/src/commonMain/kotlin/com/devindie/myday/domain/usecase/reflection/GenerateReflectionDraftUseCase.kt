package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.model.reflection.Draft
import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.model.reflection.ReflectionConstants
import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.domain.reflection.ContentSufficiency
import com.devindie.myday.domain.reflection.SourceTruncation
import com.devindie.myday.domain.repository.AiKeyRepository
import com.devindie.myday.domain.repository.DailyNoteRepository
import com.devindie.myday.domain.repository.DraftRepository
import com.devindie.myday.domain.repository.ReflectionPrefsRepository
import com.devindie.myday.domain.repository.ReflectionRepository

class GenerateReflectionDraftUseCase(
    private val dailyNotes: DailyNoteRepository,
    private val drafts: DraftRepository,
    private val keys: AiKeyRepository,
    private val prefs: ReflectionPrefsRepository,
    private val reflections: ReflectionRepository,
    private val clock: () -> Long,
    private val todayIso: () -> IsoDate,
) {
    suspend operator fun invoke(): Result<Draft> = runCatching {
        val p = prefs.get()
        if (!p.consentAccepted) throw ReflectionError.ConsentRequired
        val key = keys.getOpenRouterKey() ?: throw ReflectionError.KeyMissing
        if (dailyNotes.getVaultLink() == null) throw ReflectionError.VaultNotLinked
        val note =
            dailyNotes.resolveAndRead(todayIso()).getOrThrow()
                ?: throw ReflectionError.DailyNoteMissing
        if (!ContentSufficiency.isSufficient(note.body)) throw ReflectionError.InsufficientContent
        val truncated = SourceTruncation.fromEnd(note.body)
        val model =
            p.modelOverride?.takeIf { it.isNotBlank() }
                ?: ReflectionConstants.DEFAULT_OPENROUTER_MODEL
        val markdown = reflections.generateMarkdown(truncated.text, model, key).getOrThrow()
        val draft =
            Draft(
                date = note.ref.date,
                markdown = markdown,
                sourceContentHash = note.contentHash,
                sourceTruncated = truncated.truncated,
                generatedAtEpochMs = clock(),
            )
        drafts.save(draft)
        draft
    }
}
