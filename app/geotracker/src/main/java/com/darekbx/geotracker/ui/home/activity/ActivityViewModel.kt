package com.darekbx.geotracker.ui.home.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.repository.model.ActivityData
import com.darekbx.geotracker.domain.usecase.GetActivityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ActivityUiState {
    object Idle : ActivityUiState()
    object InProgress : ActivityUiState()
    class Done(val data: List<ActivityData>) : ActivityUiState()
}

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val getActivityUseCase: GetActivityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ActivityUiState>(ActivityUiState.Idle)
    val uiState: Flow<ActivityUiState>
        get() = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = ActivityUiState.InProgress
            val data = getActivityUseCase.getActivityData()
            _uiState.value = ActivityUiState.Done(data)
        }
    }
}
