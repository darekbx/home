package com.darekbx.stocks.ui.stocks

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.stocks.model.RateInfo
import com.darekbx.stocks.data.StocksRepository
import com.darekbx.stocks.model.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState {
    object InProgress : UiState()
    object Idle : UiState()
}

@HiltViewModel
class StocksViewModel @Inject constructor(
    private val stocksRepository: StocksRepository
) : ViewModel() {

    var rateInfoList = mutableStateListOf<RateInfo>()

    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState>
        get() = _uiState

    fun loadRates() {
        viewModelScope.launch {
            _uiState.value = UiState.InProgress

            val currencies = stocksRepository.currencies()
            currencies.forEach { currency ->
                stocksRepository.refreshCurrency(currency)
                val rates = stocksRepository.rates(currency.id!!).map { it.value }
                val step = calculateStep(rates[0])
                val status = obtainStatus(rates)
                rateInfoList.add(0, RateInfo(rates, currency.label, step, Color(160, 160, 160), status))
                _uiState.value = UiState.Idle
            }
        }
    }

    private fun calculateStep(first: Double): Float {
        return when {
            first < 10.0 -> 0.025f
            first < 10000.0 -> 100F
            else -> 1000F
        }
    }

    private fun obtainStatus(rates: List<Double>): Status {
        val last = rates.last()
        val penultimate = rates[rates.size - 2]
        return when {
            last > penultimate -> Status.PLUS
            last < penultimate -> Status.MINUS
            else -> Status.EQUAL
        }
    }
}
