package com.darekbx.emailbot.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.darekbx.emailbot.EmailBotMainActivity
import com.darekbx.emailbot.R
import kotlin.apply
import kotlin.jvm.java

class BotNotificationManager(
    private val context: Context,
    private val notificationManager: NotificationManager
) {

    fun showNotification(title: String, message: String) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Run clean up in background",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.email_bot_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setSilent(true)
            .setContentIntent(createPendingIntent())
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, EmailBotMainActivity::class.java).apply {
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
        const val CHANNEL_ID = "bot_results_channel"
        const val NOTIFICATION_ID = 53117
    }
}
