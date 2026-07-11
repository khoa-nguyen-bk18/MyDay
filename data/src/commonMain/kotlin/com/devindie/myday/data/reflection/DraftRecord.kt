package com.devindie.myday.data.reflection

import com.devindie.myday.domain.model.reflection.Draft
import com.devindie.myday.domain.model.reflection.IsoDate
import kotlinx.serialization.Serializable

@Serializable
internal data class DraftRecord(
    val date: IsoDate,
    val markdown: String,
    val sourceContentHash: String,
    val sourceTruncated: Boolean,
    val generatedAtEpochMs: Long,
)

@Serializable
internal data class DraftStorePayload(val drafts: Map<IsoDate, DraftRecord> = emptyMap())

internal fun Draft.toRecord(): DraftRecord = DraftRecord(
    date = date,
    markdown = markdown,
    sourceContentHash = sourceContentHash,
    sourceTruncated = sourceTruncated,
    generatedAtEpochMs = generatedAtEpochMs,
)

internal fun DraftRecord.toDomain(): Draft = Draft(
    date = date,
    markdown = markdown,
    sourceContentHash = sourceContentHash,
    sourceTruncated = sourceTruncated,
    generatedAtEpochMs = generatedAtEpochMs,
)
