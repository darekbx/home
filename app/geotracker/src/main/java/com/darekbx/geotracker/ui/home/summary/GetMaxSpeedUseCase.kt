package com.darekbx.geotracker.ui.home.summary

import com.darekbx.geotracker.repository.BaseHomeRepository
import com.darekbx.geotracker.utils.SpeedUtils
import javax.inject.Inject

class GetMaxSpeedUseCase @Inject constructor(
    private val homeRepository: BaseHomeRepository
) {
    suspend fun getMaxSpeed(): Float {
        val speed = homeRepository.fetchMaxSpeed()?.speed
            ?: return -1F
        return SpeedUtils.msToKm(speed)
    }
}
