package com.darekbx.notebookcheckreader.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.darekbx.notebookcheckreader.NotebookcheckActivity
import com.darekbx.notebookcheckreader.R

class RssNotificationManager(
    private val context: Context,
    private val notificationManager: NotificationManager
) {

    fun showNotification(title: String, message: String) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Run refresh in background",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_rss_feed)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setSilent(true)
            .setContentIntent(createPendingIntent())
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, NotebookcheckActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        const val CHANNEL_ID = "work_results_channel"
        const val NOTIFICATION_ID = 51237
    }
}
