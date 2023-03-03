package com.darekbx.fuel.ui.chart

import androidx.lifecycle.ViewModel
import com.darekbx.fuel.data.FuelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val fuelRepository: FuelRepository
) : ViewModel() {

    val entries = fuelRepository.getEntries().map { it.reversed() }
}
