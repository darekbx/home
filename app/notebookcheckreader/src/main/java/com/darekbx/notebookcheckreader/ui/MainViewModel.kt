package com.darekbx.notebookcheckreader.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.notebookcheckreader.domain.DeleteOldItemsUseCase
import com.darekbx.notebookcheckreader.domain.FetchFavouritesCountUseCase
import com.darekbx.notebookcheckreader.domain.FetchItemsCountUseCase
import com.darekbx.notebookcheckreader.domain.SynchronizeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MainUiState {
    object Idle : MainUiState
    object Loading : MainUiState
    data class Error(val message: String) : MainUiState
    data class Success(val count: Int) : MainUiState
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val synchronizeUseCase: SynchronizeUseCase,
    private val fetchFavouritesCountUseCase: FetchFavouritesCountUseCase,
    private val deleteOldItemsUseCase: DeleteOldItemsUseCase,
    private val itemsCountUseCase: FetchItemsCountUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Idle)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun favouritesCount() = fetchFavouritesCountUseCase()

    fun itemsCount() = itemsCountUseCase()

    fun deleteOldItems() {
        viewModelScope.launch {
            try {
                deleteOldItemsUseCase()
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun synchronize() {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            try {
                val itemsCount = synchronizeUseCase()
                _uiState.value = MainUiState.Success(itemsCount)
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
