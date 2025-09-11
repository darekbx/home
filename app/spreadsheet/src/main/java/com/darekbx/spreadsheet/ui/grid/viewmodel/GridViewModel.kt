package com.darekbx.spreadsheet.ui.grid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.spreadsheet.ui.grid.bus.SpreadSheetBus
import com.darekbx.spreadsheet.domain.SpreadSheetUseCases
import com.darekbx.spreadsheet.model.SpreadSheet
import com.darekbx.spreadsheet.ui.grid.bus.SpreadSheetModification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class GridUiState {
    data class Success(
        val spreadSheets: List<SpreadSheet>,
        val currentSpreadSheet: SpreadSheet?
    ) : GridUiState()

    data class Error(val exception: Exception) : GridUiState()
    object Loading : GridUiState()
    object Idle : GridUiState()
}

@HiltViewModel
class GridViewModel @Inject constructor(
    private val spreadSheetUseCases: SpreadSheetUseCases,
    val spreadSheetBus: SpreadSheetBus,
    val cellLoader: CellsLoader
) : ViewModel() {

    private val _uiState = MutableStateFlow<GridUiState>(GridUiState.Idle)
    val uiState: StateFlow<GridUiState> = _uiState.asStateFlow()

    private var currentSheet: SpreadSheet? = null

    init {
        viewModelScope.launch {
            spreadSheetBus.listenForModification().collect { modification ->
                applyChanges(modification)
            }
        }
    }

    fun setCurrentSheetUid(spreadSheet: SpreadSheet) {
        currentSheet = spreadSheet
    }

    fun fetchSpreadSheets(sheetUid: String) {
        viewModelScope.launch {
            _uiState.value = GridUiState.Loading
            delay(500) // To show loading state

            try {
                val parentSpreadSheet = spreadSheetUseCases.fetchSheet(sheetUid)
                    ?: throw IllegalArgumentException("Sheet with UID $sheetUid not found")
                val childSpreadSheets = spreadSheetUseCases.fetchSheets(sheetUid)
                val spreadSheets = listOf(parentSpreadSheet) + childSpreadSheets

                if (currentSheet != null) {
                    currentSheet = spreadSheets.firstOrNull { it.uid == currentSheet?.uid }
                }

                _uiState.value = GridUiState.Success(spreadSheets, currentSheet)
            } catch (e: Exception) {
                _uiState.value = GridUiState.Error(e)
            }
        }
    }

    fun createSheet(parentUid: String, name: String, columns: Int, rows: Int) {
        viewModelScope.launch {
            _uiState.value = GridUiState.Loading
            try {
                spreadSheetUseCases.addSheet(parentUid, parentName = "", name, columns, rows)
                fetchSpreadSheets(parentUid)
            } catch (e: Exception) {
                _uiState.value = GridUiState.Error(e)
            }
        }
    }

    fun deleteSheet(sheet: SpreadSheet) {
        viewModelScope.launch {
            _uiState.value = GridUiState.Loading
            try {
                spreadSheetUseCases.deleteSheet(sheet)
                val parentUid = sheet.parentUid ?: ""
                fetchSpreadSheets(parentUid)
            } catch (e: Exception) {
                _uiState.value = GridUiState.Error(e)
            }
        }
    }

    private suspend fun applyChanges(modification: SpreadSheetModification) {
        currentSheet?.uid?.let { spreadSheetUid ->
            when (modification) {
                is SpreadSheetModification.AddRow -> {
                    spreadSheetUseCases.addRow(modification.rowIndex, spreadSheetUid)
                    spreadSheetBus.reloadCells()
                }

                is SpreadSheetModification.DeleteRow -> {
                    spreadSheetUseCases.deleteRow(modification.rowIndex, spreadSheetUid)
                    spreadSheetBus.reloadCells()
                }

                is SpreadSheetModification.AddColumn -> {
                    spreadSheetUseCases.addColumn(modification.columnIndex, spreadSheetUid)
                    spreadSheetBus.reloadCells()
                }

                is SpreadSheetModification.DeleteColumn -> {
                    spreadSheetUseCases.deleteColumn(modification.columnIndex, spreadSheetUid)
                    spreadSheetBus.reloadCells()
                }

                is SpreadSheetModification.ChangeColumnWidth -> {
                    spreadSheetUseCases.changeColumnWidth(
                        modification.columnIndex,
                        modification.newWidth,
                        modification.newAlign,
                        spreadSheetUid
                    )
                    spreadSheetBus.reloadCells()
                }
            }
        }
        currentSheet?.let {
            spreadSheetUseCases.updateParentUpdated(it.parentUid ?: it.uid)
        }
    }

    fun resetState() {
        _uiState.value = GridUiState.Idle
    }
}
