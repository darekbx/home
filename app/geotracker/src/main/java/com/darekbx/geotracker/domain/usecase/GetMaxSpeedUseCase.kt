package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.utils.SpeedUtils
import javax.inject.Inject

class GetMaxSpeedUseCase @Inject constructor(
    private val repository: BaseRepository
) {
    suspend fun getMaxSpeed(): Float {
        val speed = repository.fetchMaxSpeed()?.speed
            ?: return -1F
        return SpeedUtils.msToKm(speed)
    }
}
