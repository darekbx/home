package com.darekbx.emailbot.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.emailbot.BuildConfig
import com.darekbx.emailbot.bot.CleanUpBot
import com.darekbx.emailbot.repository.RefreshBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MainUiState {
    data object Idle : MainUiState
    data object Loading : MainUiState
    data class Error(val e: Throwable) : MainUiState
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val cleanUpBot: CleanUpBot,
    private val refreshBus: RefreshBus,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Idle)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun cleanUp() {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            try {
                cleanUpBot.cleanUp()
                _uiState.value = MainUiState.Idle
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                _uiState.value = MainUiState.Error(e)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            refreshBus.publishChanges()
        }
    }
}
