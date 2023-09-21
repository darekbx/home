package com.darekbx.geotracker.ui.trip.charts

import androidx.lifecycle.ViewModel
import com.darekbx.geotracker.domain.usecase.GetTrackWithPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel @Inject constructor(
    private val getTrackWithPoints: GetTrackWithPoints
) : ViewModel() {

    fun fetchTripSpeed(tripId: Long) = flow {
        val points = getTrackWithPoints(tripId).points
        emit(points.map { it.speed })
    }

    fun fetchTripAltitude(tripId: Long) = flow {
        val points = getTrackWithPoints(tripId).points
        emit(points.map { it.altitude.toFloat() })
    }
}