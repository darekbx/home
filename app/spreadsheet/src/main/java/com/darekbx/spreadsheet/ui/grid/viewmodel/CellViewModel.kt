package com.darekbx.spreadsheet.ui.grid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.spreadsheet.domain.CellUseCases
import com.darekbx.spreadsheet.model.Cell
import com.darekbx.spreadsheet.ui.grid.functions.Function.Companion.toFunction
import com.darekbx.spreadsheet.ui.grid.functions.FunctionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CellViewModel @Inject constructor(private val cellUseCases: CellUseCases) : ViewModel() {

    private val _cellChanges = MutableSharedFlow<Cell>()
    val cellChanges: SharedFlow<Cell> = _cellChanges.asSharedFlow()

    fun updateCell(cell: Cell) {
        viewModelScope.launch {
            cellUseCases.updateCell(cell)
            _cellChanges.emit(cell)
        }
    }

    suspend fun invokeFunction(cell: Cell): Result<String>? {
        if (!cell.hasFormula) {
            return null
        }

        val columnIndex = cell.columnIndex
        val functionResult = cell.formula.toFunction()

        val function = functionResult.getOrNull()
            ?: return Result.failure(Exception("Not a function"))

        if (function.type == FunctionType.EMPTY) {
            return null
        }

        cellUseCases.fetchCells(cell.sheetUid, columnIndex, function.range)
            .map { it.value.toDoubleOrNull() ?: 0.0 }
            .let { values ->
                return try {
                    val result = when (function.type) {
                        FunctionType.SUM -> values.sum()
                        FunctionType.AVG -> values.average()
                        FunctionType.MIN -> values.min()
                        FunctionType.MAX -> values.max()
                        FunctionType.EMPTY -> 0.0
                    }
                    Result.success(result.toString())
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }

    fun getCellReferences(cell: Cell): List<Int> {
        if (!cell.hasFormula) return emptyList()
        val functionResult = cell.formula.toFunction()
        return functionResult.getOrNull()?.range ?: emptyList()
    }
}
