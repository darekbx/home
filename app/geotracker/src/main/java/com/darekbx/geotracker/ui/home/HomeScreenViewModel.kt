package com.darekbx.geotracker.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.darekbx.geotracker.domain.usecase.GetRecordingStateUseCase
import com.darekbx.geotracker.system.BaseLocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val locationManager: BaseLocationManager,
    private val getRecordingStateUseCase: GetRecordingStateUseCase
) : ViewModel() {

    fun isLocationEnabled() = mutableStateOf(locationManager.isLocationEnabled())
}