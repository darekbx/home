package com.darekbx.stocks.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.darekbx.stocks.data.StocksRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Duration

@HiltWorker
class StocksWorker @AssistedInject constructor(
    private val context: Context,
    workerParameters: WorkerParameters,
    @Assisted private val stocksRepository: StocksRepository
) : CoroutineWorker(context, workerParameters) {

    companion object {

        private val uniqueWorkName = "StocksWorker"

        fun enqueue(context: Context, force: Boolean = false) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<StocksWorker>(
                Duration.ofMinutes(30)
            )
            var workPolicy = ExistingPeriodicWorkPolicy.KEEP

            if (force) {
                workPolicy = ExistingPeriodicWorkPolicy.REPLACE
            }

            manager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                workPolicy,
                requestBuilder.build()
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
        }
    }

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(StocksWidget::class.java)
        return try {
            setWidgetState(glanceIds, StocksInfo.Loading)
            setWidgetState(glanceIds, stocksRepository.getStocksInfo())

            Result.success()
        } catch (e: Exception) {
            setWidgetState(glanceIds, StocksInfo.Unavailable(e.message.orEmpty()))
            if (runAttemptCount < 2) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun setWidgetState(glanceIds: List<GlanceId>, newState: StocksInfo) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = StocksInfoStateDefinition,
                glanceId = glanceId,
                updateState = { newState }
            )
        }
        StocksWidget().updateAll(context)
    }
}