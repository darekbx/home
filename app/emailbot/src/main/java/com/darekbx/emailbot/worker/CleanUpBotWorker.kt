package com.darekbx.notebookcheckreader.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.darekbx.emailbot.bot.CleanUpBot
import com.darekbx.emailbot.worker.BotNotificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class CleanUpBotWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    val cleanUpBot: CleanUpBot,
    val notificationManager: BotNotificationManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val (removedCount, emailsCount, totalRemovedCount) = cleanUpBot.cleanUp()
            notificationManager.showNotification(
                "Clean up completed",
                "$emailsCount emails, removed $removedCount spam emails.\nOverall removed: $totalRemovedCount"
            )
            Result.success()
        } catch (e: Exception) {
            Log.e("CoroutineWorker", "Error in worker", e)
            notificationManager.showNotification(
                "Failed to clean up",
                e.message ?: "Unknown error"
            )
            Result.failure()
        }
    }
}
