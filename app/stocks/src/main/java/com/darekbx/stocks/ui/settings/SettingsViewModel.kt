package com.darekbx.stocks.ui.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.stocks.data.ArdustocksImport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    fun importFromArdustocks() {
        viewModelScope.launch {
            _uiState.value = UiState.InProgress

            ardustocksImport.importFromArdustocks()

            delay(2000L)
            _uiState.value = UiState.Idle
        }
    }
}
