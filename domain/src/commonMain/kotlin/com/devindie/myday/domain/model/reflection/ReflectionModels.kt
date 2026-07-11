package com.devindie.myday.domain.model.reflection

/** Calendar date as ISO `YYYY-MM-DD` (domain stays free of kotlinx-datetime if not already used). */
typealias IsoDate = String

data class VaultLink(val tokenValue: String)

data class DailyNoteRef(val date: IsoDate, val relativePath: String, val resolution: DailyNoteResolution)

enum class DailyNoteResolution {
    PeriodicNotes,
    CoreDailyNotes,
    Fallback,
}

data class DailyNoteContent(val ref: DailyNoteRef, val body: String, val contentHash: String)

data class Draft(
    val date: IsoDate,
    val markdown: String,
    val sourceContentHash: String,
    val sourceTruncated: Boolean,
    val generatedAtEpochMs: Long,
)

data class ReflectionDocument(val date: IsoDate, val markdown: String, val relativePath: String)

data class ReflectionPrefs(
    val consentAccepted: Boolean = false,
    val featureEnabled: Boolean = false,
    val windowStartMinuteOfDay: Int = ReflectionConstants.DEFAULT_WINDOW_START_MINUTE,
    val windowEndMinuteOfDay: Int = ReflectionConstants.DEFAULT_WINDOW_END_MINUTE,
    val reflectionFolder: String = ReflectionConstants.DEFAULT_REFLECTION_FOLDER,
    val modelOverride: String? = null,
    val usedFallbackPathNoticeShown: Boolean = false,
)

sealed class ReflectionError : Exception() {
    data object VaultNotLinked : ReflectionError()

    data object VaultPermissionDenied : ReflectionError()

    data object ConsentRequired : ReflectionError()

    data object KeyMissing : ReflectionError()

    data object DailyNoteMissing : ReflectionError()

    data object InsufficientContent : ReflectionError()

    data object AlreadyExists : ReflectionError()

    data object Network : ReflectionError()

    data class Provider(val code: Int?, override val message: String?) : ReflectionError()

    data object MalformedOutput : ReflectionError()

    data object Cancelled : ReflectionError()

    data object OutsideWindow : ReflectionError()
}
