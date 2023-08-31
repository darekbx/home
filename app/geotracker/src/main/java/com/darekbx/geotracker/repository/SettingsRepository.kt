package com.darekbx.geotracker.repository

import javax.inject.Inject

class SettingsRepository @Inject constructor() {

    fun isDarkMode() = true

    suspend fun nthPointsToSkip() = 2
}