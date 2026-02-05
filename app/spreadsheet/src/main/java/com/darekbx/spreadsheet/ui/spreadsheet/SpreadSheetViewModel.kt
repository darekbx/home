package com.darekbx.spreadsheet.ui.spreadsheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.spreadsheet.domain.SpreadSheetUseCases
import com.darekbx.spreadsheet.domain.SyncStatus
import com.darekbx.spreadsheet.domain.SynchronizeUseCase
import com.darekbx.spreadsheet.model.SpreadSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class SpreadSheetUIState {
    data class Success(val syncRequired: Boolean, val items: List<SpreadSheet>) :
        SpreadSheetUIState()

    data class Error(val exception: Exception) : SpreadSheetUIState()
    object Loading : SpreadSheetUIState()
    object Idle : SpreadSheetUIState()
}

@HiltViewModel
class SpreadSheetViewModel @Inject constructor(
    private val spreadSheetUseCases: SpreadSheetUseCases,
    private val synchronizeUseCase: SynchronizeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SpreadSheetUIState>(SpreadSheetUIState.Idle)
    private val _syncState = MutableStateFlow<SyncStatus>(SyncStatus.Idle)

    val uiState: StateFlow<SpreadSheetUIState> = _uiState.asStateFlow()
    val syncState: StateFlow<SyncStatus> = _syncState.asStateFlow()

    fun synchronize() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                synchronizeUseCase.synchronize().collect { state ->
                    _syncState.value = state
                }
            }
        }
    }

    fun fetchSheets() {
        viewModelScope.launch {
            _uiState.value = SpreadSheetUIState.Loading
            try {
                val sheets = spreadSheetUseCases.fetchSheets(parentUid = null)
                val syncRequired = if (sheets.isNotEmpty()) {
                    synchronizeUseCase.shouldSynchronize()
                } else {
                    false
                }
                _uiState.value = SpreadSheetUIState.Success(syncRequired, sheets)
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
