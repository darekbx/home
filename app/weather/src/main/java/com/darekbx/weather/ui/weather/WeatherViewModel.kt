package com.darekbx.weather.ui.weather

import androidx.lifecycle.ViewModel
import com.darekbx.weather.data.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    val weatherConditions = flow {
        val data = weatherRepository.getImagesUrls()
        emit(data)
    }
}