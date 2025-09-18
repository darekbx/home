package com.darekbx.home

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.darekbx.notebookcheckreader.worker.CleanUpBotWorker
import com.darekbx.notebookcheckreader.worker.DataRefreshWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class HomeApplication: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        schedulePeriodicDataRefresh()
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()

    private fun schedulePeriodicDataRefresh() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val cleanUpWorkRequest = PeriodicWorkRequestBuilder<CleanUpBotWorker>(8, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        val dataRefreshWorkRequest = PeriodicWorkRequestBuilder<DataRefreshWorker>(8, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DataRefreshWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dataRefreshWorkRequest
        )

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CleanUpBotWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dataRefreshWorkRequest
        )
    }
}
