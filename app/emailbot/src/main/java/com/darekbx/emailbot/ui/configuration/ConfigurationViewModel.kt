package com.darekbx.emailbot.ui.configuration.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.emailbot.imap.Connection
import com.darekbx.emailbot.model.ConfigurationInfo
import com.darekbx.emailbot.repository.storage.EncryptedConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ConfigurationUiState {
    data object Idle : ConfigurationUiState
    data object Loading : ConfigurationUiState
    data object Saved : ConfigurationUiState
    data class Success(val configurationInfo: ConfigurationInfo) : ConfigurationUiState
    data class Error(val e: Throwable) : ConfigurationUiState
    data class CheckResult(val result: Boolean) : ConfigurationUiState
}

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val encryptedConfiguration: EncryptedConfiguration,
    private val connection: Connection
): ViewModel() {

    private val _uiState = MutableStateFlow<ConfigurationUiState>(ConfigurationUiState.Idle)
    val uiState: StateFlow<ConfigurationUiState> = _uiState.asStateFlow()

    fun fetchConfiguration() {
        viewModelScope.launch {
            _uiState.value = ConfigurationUiState.Loading
            delay(500)
            encryptedConfiguration.loadConfiguration()?.let {
                _uiState.value = ConfigurationUiState.Saved
            } ?: run {
                _uiState.value = ConfigurationUiState.Idle
            }
        }
    }

    fun checkConnection(configurationInfo: ConfigurationInfo) {
        viewModelScope.launch {
            _uiState.value = ConfigurationUiState.Loading
            connection
                .verifyConfiguration(configurationInfo)
                .onFailure { e -> _uiState.value = ConfigurationUiState.Error(e) }
                .onSuccess { isValid -> _uiState.value = ConfigurationUiState.CheckResult(isValid) }
        }
    }

    fun saveConfiguration(configurationInfo: ConfigurationInfo) {
        viewModelScope.launch {
            _uiState.value = ConfigurationUiState.Loading
            delay(500)
            encryptedConfiguration.saveConfiguration(configurationInfo)
            _uiState.value = ConfigurationUiState.Saved
        }
    }

    fun resetState() {
        _uiState.value = ConfigurationUiState.Idle
    }
}
