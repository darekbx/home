package com.darekbx.common.utils

import android.content.Context
import android.net.ConnectivityManager

object ConnectionUtils {

    fun isInternetConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.getActiveNetwork() != null && cm.getNetworkCapabilities(cm.getActiveNetwork()) != null
    }
}
