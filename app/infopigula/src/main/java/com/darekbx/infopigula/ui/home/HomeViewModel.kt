package com.darekbx.infopigula.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.infopigula.domain.GetNewsUseCase
import com.darekbx.infopigula.model.NewsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Idle : HomeUiState()
    object InProgress : HomeUiState()
    class Done(val data: NewsResponse) : HomeUiState()
    class Failed(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: Flow<HomeUiState>
        get() = _uiState

    fun resetState() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Idle
        }
    }

    fun loadNews() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.InProgress

            val result = getNewsUseCase.invoke()

            if (result.isSuccess) {
                _uiState.value = result.getOrNull()
                    ?.let { newsWrapper -> HomeUiState.Done(newsWrapper) }
                    ?: HomeUiState.Failed("Data is empty!")
            } else {
                _uiState.value =
                    HomeUiState.Failed(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}