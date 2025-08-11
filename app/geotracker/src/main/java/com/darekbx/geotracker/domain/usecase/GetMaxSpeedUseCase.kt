package com.darekbx.geotracker.domain.usecase

import android.util.Log
import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.utils.SpeedUtils
import javax.inject.Inject

class GetMaxSpeedUseCase @Inject constructor(
    private val repository: BaseRepository
) {
    // Year max speed
    suspend fun getMaxSpeed(): Float {
        val point = repository.fetchMaxSpeed()
        Log.v(TAG, "TrackId with max speed: ${point?.trackId}")
        val speed = point?.speed ?: return -1F
        return SpeedUtils.msToKm(speed)
    }

    companion object {
        private const val TAG = "GetMaxSpeedUseCase"
    }
}
