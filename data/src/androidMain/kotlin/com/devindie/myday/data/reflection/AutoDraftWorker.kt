package com.devindie.myday.data.reflection

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.devindie.myday.domain.model.reflection.AutoDraftResult
import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.domain.usecase.reflection.RunAutoDraftUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AutoDraftWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params),
    KoinComponent {
    private val runAutoDraft: RunAutoDraftUseCase by inject()

    override suspend fun doWork(): Result = try {
        when (runAutoDraft()) {
            is AutoDraftResult.Generated,
            AutoDraftResult.SkippedFeatureDisabled,
            AutoDraftResult.SkippedOutsideWindow,
            AutoDraftResult.SkippedDraftExists,
            -> Result.success()
        }
    } catch (_: ReflectionError.Network) {
        Result.retry()
    } catch (_: ReflectionError.Provider) {
        Result.retry()
    } catch (_: ReflectionError) {
        // Consent/key/insufficient/etc. — do not retry forever.
        Result.success()
    } catch (_: Exception) {
        Result.failure()
    }
}
