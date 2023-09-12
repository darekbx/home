package com.darekbx.geotracker.service

import android.content.Intent
import android.content.pm.ServiceInfo
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.darekbx.geotracker.R
import com.darekbx.geotracker.location.AddLocationUseCase
import com.darekbx.geotracker.location.LocationCollector
import com.darekbx.geotracker.system.BaseLocationManager
import com.darekbx.geotracker.utils.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : LifecycleService() {

    @Inject
    lateinit var notificationUtils: NotificationUtils

    @Inject
    lateinit var locationManager: BaseLocationManager

    @Inject
    lateinit var locationCollector: LocationCollector

    @Inject
    lateinit var addLocationUseCase: AddLocationUseCase

    private var locationFlow: Job? = null

    override fun onCreate() {
        super.onCreate()

        val notification = notificationUtils.createNotification(
            getString(R.string.geotracker_app_name),
            getString(R.string.notification_text)
        )

        startForeground(
            NotificationUtils.NOTIFICATION_ID, notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        )

        IS_RUNNING = true
    }

    override fun onDestroy() {
        super.onDestroy()
        locationFlow?.cancel()
        IS_RUNNING = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!locationManager.isLocationEnabled()) {
            Log.e(TAG, "Location is not enabled!")
            return super.onStartCommand(intent, flags, startId)
        }

        locationFlow = locationCollector.locationFlow()
            .onEach { location -> addLocationUseCase(location) }
            .launchIn(lifecycleScope)

        return START_STICKY
    }

    companion object {
        var IS_RUNNING = false
        private const val TAG = "LocationService"
    }
}
