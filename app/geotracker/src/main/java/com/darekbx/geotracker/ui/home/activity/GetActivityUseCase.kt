package com.darekbx.geotracker.ui.home.activity

import com.darekbx.geotracker.repository.BaseHomeRepository
import com.darekbx.geotracker.repository.model.ActivityData
import kotlinx.coroutines.delay
import javax.inject.Inject

class GetActivityUseCase @Inject constructor(
    private val homeRepository: BaseHomeRepository
) {
    suspend fun getActivityData(): ActivityData {
        delay(1000L)
        return ActivityData(1)
    }
}