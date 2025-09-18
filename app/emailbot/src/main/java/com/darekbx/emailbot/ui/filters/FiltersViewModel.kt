package com.darekbx.emailbot.ui.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.emailbot.domain.DeleteSpamFilterUseCase
import com.darekbx.emailbot.domain.FetchSpamFiltersUseCase
import com.darekbx.emailbot.model.SpamFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FiltersUiState {
    data object Idle : FiltersUiState
    data object Loading : FiltersUiState
    data class Error(val e: Throwable) : FiltersUiState
    data class Success(val emails: List<SpamFilter>) : FiltersUiState
}

@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val fetchSmapFiters: FetchSpamFiltersUseCase,
    private val deleteSpamFilterUseCase: DeleteSpamFilterUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<FiltersUiState>(FiltersUiState.Idle)
    val uiState: StateFlow<FiltersUiState> = _uiState.asStateFlow()

    fun fetchSpamFilters() {
        viewModelScope.launch {
            _uiState.value = FiltersUiState.Loading
            try {
                val spamFilters = fetchSmapFiters()
                    .map { SpamFilter.toModel(it) }
                _uiState.value = FiltersUiState.Success(spamFilters)
            } catch (e: Exception) {
                _uiState.value = FiltersUiState.Error(e)
            }
        }
    }

    fun deleteSpamFilter(id: String) {
        viewModelScope.launch {
            try {
                deleteSpamFilterUseCase(id)
                fetchSpamFilters()
            } catch (e: Exception) {
                _uiState.value = FiltersUiState.Error(e)
            }
        }
    }
}
