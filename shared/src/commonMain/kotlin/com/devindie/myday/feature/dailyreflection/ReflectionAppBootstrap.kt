package com.devindie.myday.feature.dailyreflection

import com.devindie.myday.domain.reflection.ReflectionInjection
import com.devindie.myday.domain.usecase.reflection.EnsureReflectionScheduleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

/** Koin named qualifier for debug Auto-Draft tools (bound in app shells). */
const val REFLECTION_DEBUG_TOOLS_QUALIFIER = ReflectionInjection.DEBUG_TOOLS

/** Re-apply WorkManager / iOS Auto-Draft schedule from persisted prefs after Koin starts. */
fun bootstrapReflectionSchedule(scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)) {
    scope.launch {
        runCatching { getKoin().get<EnsureReflectionScheduleUseCase>()() }
    }
}
