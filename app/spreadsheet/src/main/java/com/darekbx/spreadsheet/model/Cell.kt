package com.darekbx.spreadsheet.model

import com.darekbx.spreadsheet.domain.SpreadSheetUseCases.Companion.DEFAULT_WIDTH
import com.darekbx.spreadsheet.model.Style.Companion.styleFromJson
import com.darekbx.storage.spreadsheet.entities.CellDto

data class Cell(
    val uid: String,
    val sheetUid: String,
    val rowIndex: Int,
    val columnIndex: Int,
    val value: String,
    val formula: String? = null,
    val style: String? = null,
    val width: Int = DEFAULT_WIDTH
) {
    val name: String
        get() = "${('A' + columnIndex)}${rowIndex + 1}"

    val parsedStyle: Style
        get() = style?.styleFromJson() ?: Style()

    val hasFormula: Boolean
        get() = !formula.isNullOrEmpty()

    companion object {
        fun CellDto.fromEntity(): Cell {
            return Cell(
                uid = uid,
                sheetUid = sheetUid,
                rowIndex = rowIndex,
                columnIndex = columnIndex,
                value = value,
                formula = formula,
                style = style,
                width = width
            )
        }
    }
}
