package com.devindie.myday.domain.usecase.reflection

import com.devindie.myday.domain.model.reflection.ReflectionPrefs

data class ReflectionSetupState(val prefs: ReflectionPrefs, val hasOpenRouterKey: Boolean, val vaultLinked: Boolean)
