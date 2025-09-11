package com.darekbx.spreadsheet.ui.grid.bus

import com.darekbx.spreadsheet.model.Style
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class SpreadSheetBus {

    private val spreadSheetRefreshFlow = MutableSharedFlow<Long>()

    private val spreadSheetModificationFlow = MutableSharedFlow<SpreadSheetModification>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    suspend fun addRow(rowIndex: Int) {
        spreadSheetModificationFlow.emit(SpreadSheetModification.AddRow(rowIndex))
    }

    suspend fun deleteRow(rowIndex: Int) {
        spreadSheetModificationFlow.emit(SpreadSheetModification.DeleteRow(rowIndex))
    }

    suspend fun addColumn(columnIndex: Int) {
        spreadSheetModificationFlow.emit(SpreadSheetModification.AddColumn(columnIndex))
    }

    suspend fun deleteColumn(columnIndex: Int) {
        spreadSheetModificationFlow.emit(SpreadSheetModification.DeleteColumn(columnIndex))
    }

    suspend fun changeColumnStyle(columnIndex: Int, newWidth: Int, newAlign: Style.Align) {
        spreadSheetModificationFlow.emit(
            SpreadSheetModification.ChangeColumnWidth(
                columnIndex,
                newWidth,
                newAlign
            )
        )
    }

    suspend fun reloadCells() {
        spreadSheetRefreshFlow.emit(System.currentTimeMillis())
    }

    fun listenForModification(): Flow<SpreadSheetModification> {
        return spreadSheetModificationFlow
    }

    fun listenForRefresh(): Flow<Long> {
        return spreadSheetRefreshFlow
    }
}
