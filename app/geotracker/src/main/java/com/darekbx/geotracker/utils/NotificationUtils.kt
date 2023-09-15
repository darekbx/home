package com.darekbx.geotracker.utils

import android.app.*
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.darekbx.geotracker.GeoTrackerActivity
import com.darekbx.geotracker.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationUtils @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager
) {

    fun updateNotification(distance: Float, elapsedTimeMs: Long) {
        val elapsedTime = elapsedTimeMs / 1000L
        val notification = createNotification(
            context.getString(
                R.string.notification_title,
                distance / 1000F,
                DateTimeUtils.getFormattedTime(elapsedTime)
            ),
            context.getString(R.string.notification_text)
        )
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun createNotification(title: String, text: String): Notification {
        val tracksIntent = Intent(context, GeoTrackerActivity::class.java)
        val tracksPendingIntent = getActivity(context, 0,
            tracksIntent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_myplaces)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(tracksPendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        var channel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
        if (channel == null) {
            createNotificationChannel()
        }

        return builder.build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.notification_channel_title),
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val NOTIFICATION_ID = 200
        private const val NOTIFICATION_CHANNEL_ID = "location_channel_id"
    }
}