package com.darekbx.infopigula.ui.creators

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.infopigula.domain.GetCreatorsUseCase
import com.darekbx.infopigula.model.Creator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreatorsUiState {
    object Idle : CreatorsUiState()
    object InProgress : CreatorsUiState()
    class Done(val creators: List<Creator>) : CreatorsUiState()
    class Failed(val message: String) : CreatorsUiState()
}

@HiltViewModel
class CreatorsViewModel @Inject constructor(
    private val getCreatorsUseCase: GetCreatorsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreatorsUiState>(CreatorsUiState.Idle)
    val uiState: Flow<CreatorsUiState>
        get() = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = CreatorsUiState.InProgress
            val result = getCreatorsUseCase.invoke()
            if (result.isSuccess) {
                _uiState.value = CreatorsUiState.Done(result.getOrThrow())
            } else {
                _uiState.value =
                    CreatorsUiState.Failed(result.exceptionOrNull()?.message ?: "Unknown error!")
            }
        }
    }
}