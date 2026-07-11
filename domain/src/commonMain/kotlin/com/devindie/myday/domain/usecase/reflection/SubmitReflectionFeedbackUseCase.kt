package com.devindie.myday.domain.usecase.reflection

class SubmitReflectionFeedbackUseCase {
    operator fun invoke(helpful: Boolean, reason: NotHelpfulReason?): ReflectionFeedbackEvent {
        if (helpful && reason != null) {
            throw IllegalArgumentException("reason is only valid when feedback is not helpful")
        }
        return ReflectionFeedbackEvent(
            helpful = helpful,
            reason = reason?.name,
        )
    }
}
