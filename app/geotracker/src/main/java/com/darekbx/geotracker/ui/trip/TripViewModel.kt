package com.darekbx.geotracker.ui.trip

import androidx.lifecycle.ViewModel
import com.darekbx.geotracker.domain.usecase.GetTrackWithPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val getTrackWithPoints: GetTrackWithPoints
) : ViewModel() {

    fun data(trackId: Long) = flow {
        emit(getTrackWithPoints.invoke(trackId))
    }
}