package com.devindie.myday.domain.usecase.reflection

enum class NotHelpfulReason {
    TooGeneric,
    IncorrectInterpretation,
    MissedImportantDetails,
    TooLong,
    TooPersonal,
    Repetitive,
    ToneDoesNotFeelRight,
    UnexpectedContent,
}

data class ReflectionFeedbackEvent(val helpful: Boolean, val reason: String?)
