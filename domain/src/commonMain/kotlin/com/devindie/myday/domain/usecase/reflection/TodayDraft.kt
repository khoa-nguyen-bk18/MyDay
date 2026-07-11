package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.model.reflection.Draft

data class TodayDraft(val draft: Draft?, val sourceChanged: Boolean)
