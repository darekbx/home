package com.darekbx.spreadsheet.ui.grid.bus

import com.darekbx.spreadsheet.model.Style

sealed class SpreadSheetModification {
    data class AddRow(val rowIndex: Int) : SpreadSheetModification()
    data class DeleteRow(val rowIndex: Int) : SpreadSheetModification()
    data class AddColumn(val columnIndex: Int) : SpreadSheetModification()
    data class DeleteColumn(val columnIndex: Int) : SpreadSheetModification()
    data class ChangeColumnWidth(
        val columnIndex: Int,
        val newWidth: Int,
        val newAlign: Style.Align
    ) : SpreadSheetModification()
}
