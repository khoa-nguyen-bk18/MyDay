package com.devindie.myday.data.reflection

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import com.devindie.myday.domain.repository.ReflectionSchedulerPort
import java.util.concurrent.TimeUnit

class AndroidReflectionScheduler(private val context: Context) : ReflectionSchedulerPort {
    override fun reschedule(prefs: ReflectionPrefs) {
        if (!prefs.featureEnabled || !prefs.consentAccepted) {
            cancel()
            return
        }
        val request =
            PeriodicWorkRequestBuilder<AutoDraftWorker>(15, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints
                        .Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build(),
                ).build()
        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request,
            )
    }

    override fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    companion object {
        const val UNIQUE_WORK_NAME = "daily_reflection_auto_draft"
    }
}
