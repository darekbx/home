@file:OptIn(ExperimentalPermissionsApi::class)

package com.darekbx.dotpad.widget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.compose.foundation.ExperimentalFoundationApi
import com.darekbx.dotpad.MainActivity
import com.darekbx.dotpad.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalFoundationApi
class CountWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {}

    override fun onDisabled(context: Context) {}
}

@ExperimentalFoundationApi
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.count_widget_layout)
    views.setOnClickPendingIntent(
        R.id.widget_root,
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            FLAG_IMMUTABLE
        )
    )
    views.setTextViewText(R.id.appwidget_text, "${MainActivity.LAST_COUNT}")

    appWidgetManager.updateAppWidget(appWidgetId, views)
}