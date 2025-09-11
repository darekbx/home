package com.darekbx.spreadsheet.ui.grid.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.spreadsheet.domain.ImportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ImportUiState {
    data object Success : ImportUiState()
    data class Error(val exception: Exception) : ImportUiState()
    data object Loading : ImportUiState()
    data object Idle : ImportUiState()
}

@HiltViewModel
class ImportViewModel @Inject constructor(private val importUseCase: ImportUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<ImportUiState>(ImportUiState.Idle)
    val uiState: StateFlow<ImportUiState> = _uiState.asStateFlow()


    fun importSheet(spreadSheetUid: String, name: String, parentName: String, uri: Uri?) {
        viewModelScope.launch {
            _uiState.value = ImportUiState.Loading
            if (uri == null) {
                _uiState.value = ImportUiState.Error(IllegalArgumentException("File uri is empty!"))
                return@launch
            }
            try {
                importUseCase.import(spreadSheetUid, name, parentName, uri)
                _uiState.value = ImportUiState.Success
            } catch (e: Exception) {
                _uiState.value = ImportUiState.Error(e)
            }
        }
    }

    fun resetState() {
        _uiState.value = ImportUiState.Idle
    }
}
