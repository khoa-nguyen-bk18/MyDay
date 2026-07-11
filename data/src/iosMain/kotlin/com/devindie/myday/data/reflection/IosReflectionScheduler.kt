package com.devindie.myday.data.reflection

import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import com.devindie.myday.domain.repository.ReflectionSchedulerPort
import com.devindie.myday.domain.usecase.reflection.RunAutoDraftUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Best-effort Auto-Draft scheduling for iOS.
 *
 * iOS does not reliably run BYOK network work in the background the way WorkManager
 * does on Android. This implementation attempts [RunAutoDraftUseCase] when schedule
 * is (re)applied — typically after prefs change or [checkOnForeground] — and documents
 * that generation may only happen while the app is active.
 */
class IosReflectionScheduler(private val runAutoDraft: () -> RunAutoDraftUseCase) : ReflectionSchedulerPort {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var enabled: Boolean = false

    override fun reschedule(prefs: ReflectionPrefs) {
        enabled = prefs.featureEnabled && prefs.consentAccepted
        if (!enabled) return
        scope.launch {
            runCatching { runAutoDraft()() }
        }
    }

    override fun cancel() {
        enabled = false
    }

    /** Call from app lifecycle when returning to foreground. */
    fun checkOnForeground() {
        if (!enabled) return
        scope.launch {
            runCatching { runAutoDraft()() }
        }
    }
}
