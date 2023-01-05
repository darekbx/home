package com.darekbx.stocks.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class StocksWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = StocksWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        StocksWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        StocksWorker.cancel(context)
    }
}