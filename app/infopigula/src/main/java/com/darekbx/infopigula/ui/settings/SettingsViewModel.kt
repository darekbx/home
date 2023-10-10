package com.darekbx.infopigula.ui.settings

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.infopigula.repository.SettingsRepository
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
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val uiState: Flow<SettingsUiState>
        get() = _uiState

    var isDarkMode = mutableStateOf(false)
    var groups = mutableStateListOf<Int>()

    init {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.InProgress
            isDarkMode.value = settingsRepository.isDarkMode()
            groups.addAll(settingsRepository.filteredGroups())
            _uiState.value = SettingsUiState.Idle
        }
    }

    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.InProgress
            settingsRepository.saveDarkMode(isDark)
            isDarkMode.value = settingsRepository.isDarkMode()
            _uiState.value = SettingsUiState.Idle
        }
    }

    fun save(groups: String) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.InProgress
            settingsRepository.saveFilteredGroups(groups.split(",").map { it.toInt() })
            _uiState.value = SettingsUiState.Idle
        }
    }
}