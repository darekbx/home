package com.darekbx.geotracker.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.darekbx.geotracker.system.BaseLocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val locationManager: BaseLocationManager
) : ViewModel() {

    fun isLocationEnabled() = mutableStateOf(locationManager.isLocationEnabled())
}