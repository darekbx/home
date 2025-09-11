package com.darekbx.spreadsheet.ui.spreadsheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.spreadsheet.domain.SpreadSheetUseCases
import com.darekbx.spreadsheet.model.SpreadSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SpreadSheetUIState {
    data class Success(val items: List<SpreadSheet>) : SpreadSheetUIState()
    data class Error(val exception: Exception) : SpreadSheetUIState()
    object Loading : SpreadSheetUIState()
    object Idle : SpreadSheetUIState()
}

@HiltViewModel
class SpreadSheetViewModel @Inject constructor(
    private val spreadSheetUseCases: SpreadSheetUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<SpreadSheetUIState>(SpreadSheetUIState.Idle)
    val uiState: StateFlow<SpreadSheetUIState> = _uiState.asStateFlow()

    fun fetchSheets() {
        viewModelScope.launch {
            _uiState.value = SpreadSheetUIState.Loading
            try {
                val sheets = spreadSheetUseCases.fetchSheets(parentUid = null)
                _uiState.value = SpreadSheetUIState.Success(sheets)
            } catch (e: Exception) {
                _uiState.value = SpreadSheetUIState.Error(e)
            }
        }
    }

    fun createSheet(parentUid: String?, parentName: String, name: String, columns: Int, rows: Int) {
        viewModelScope.launch {
            _uiState.value = SpreadSheetUIState.Loading
            try {
                spreadSheetUseCases.addSheet(parentUid, parentName, name, columns, rows)
                fetchSheets()
            } catch (e: Exception) {
                _uiState.value = SpreadSheetUIState.Error(e)
            }
        }
    }

    fun deleteSheet(spreadSheet: SpreadSheet) {
        viewModelScope.launch {
            _uiState.value = SpreadSheetUIState.Loading
            try {
                spreadSheetUseCases.deleteSheet(spreadSheet)
                fetchSheets()
            } catch (e: Exception) {
                _uiState.value = SpreadSheetUIState.Error(e)
            }
        }
    }

    fun resetState() {
        _uiState.value = SpreadSheetUIState.Idle
    }
}
