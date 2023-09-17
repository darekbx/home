package com.darekbx.geotracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.domain.usecase.DeleteAndRestoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SettingsUiState {
    object Idle : SettingsUiState()
    object InProgress : SettingsUiState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val deleteAndRestoreUseCase: DeleteAndRestoreUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val uiState: Flow<SettingsUiState>
        get() = _uiState

    fun deleteAndRestore() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.InProgress
            deleteAndRestoreUseCase()
            _uiState.value = SettingsUiState.Idle
        }
    }
}
