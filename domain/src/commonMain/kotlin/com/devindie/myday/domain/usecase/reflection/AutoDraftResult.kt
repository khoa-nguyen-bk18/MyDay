package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.model.reflection.Draft

sealed class AutoDraftResult {
    data object SkippedOutsideWindow : AutoDraftResult()

    data object SkippedDraftExists : AutoDraftResult()

    data class Generated(val draft: Draft) : AutoDraftResult()
}
