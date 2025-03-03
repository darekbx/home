package com.darekbx.stocks.ui.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.stocks.data.ArdustocksImport
import com.darekbx.stocks.data.ArdustocksImport.Companion.RIVER_STATE
import com.darekbx.stocks.data.StockType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState {
    object InProgress : UiState()
    object Idle : UiState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val ardustocksImport: ArdustocksImport
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState>
        get() = _uiState

    fun addCustom() {
        viewModelScope.launch {
            _uiState.value = UiState.InProgress
            ardustocksImport.addCustom(StockType.RIVER_STATE, RIVER_STATE)
            _uiState.value = UiState.Idle
        }
    }

    fun importFromArdustocks() {
        viewModelScope.launch {
            _uiState.value = UiState.InProgress
            //ardustocksImport.importFromCsv()
            _uiState.value = UiState.Idle
        }
    }
}
