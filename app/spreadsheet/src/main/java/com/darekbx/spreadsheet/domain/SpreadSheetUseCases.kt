package com.darekbx.spreadsheet.domain

import com.darekbx.spreadsheet.model.Cell.Companion.fromEntity
import com.darekbx.spreadsheet.model.SpreadSheet
import com.darekbx.spreadsheet.model.SpreadSheet.Companion.fromEntity
import com.darekbx.spreadsheet.model.Style
import com.darekbx.spreadsheet.model.Style.Companion.toJson
import com.darekbx.storage.spreadsheet.CellDao
import com.darekbx.storage.spreadsheet.SpreadSheetDao
import com.darekbx.storage.spreadsheet.entities.CellDto
import com.darekbx.storage.spreadsheet.entities.SpreadSheetDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class SpreadSheetUseCases(
    private val spreadSheetDao: SpreadSheetDao,
    private val cellDao: CellDao
) {
    suspend fun addSheet(
        parentUid: String?,
        parentName: String,
        name: String,
        columns: Int,
        rows: Int
    ) {
        withContext(Dispatchers.IO) {
            val spreadSheetUid = UUID.randomUUID().toString()
            spreadSheetDao.add(
                SpreadSheetDto(
                    uid = spreadSheetUid,
                    parentName = parentName,
                    name = name,
                    parentUid = parentUid
                )
            )

            // Add cells for the new sheet
            val cellEntities = createCells(rows, columns, spreadSheetUid)
            cellDao.add(cellEntities)
        }
    }

    private fun createCells(
        rows: Int,
        columns: Int,
        spreadSheetUid: String
    ) = (0 until rows).flatMap { row ->
        (0 until columns).map { column ->
            createCell(spreadSheetUid, row, column)
        }
    }

    suspend fun fetchSheet(uid: String): SpreadSheet? =
        withContext(Dispatchers.IO) {
            spreadSheetDao.fetch(uid)?.fromEntity()
        }

    suspend fun fetchSheets(parentUid: String?): List<SpreadSheet> =
        withContext(Dispatchers.IO) {
            (if (parentUid.isNullOrEmpty()) {
                spreadSheetDao.fetchAll()
                    .map { it.fromEntity() }
                    .apply {
                        forEach { parentSheet ->
                            val childSheets = spreadSheetDao.fetchAll(parentSheet.uid)
                            parentSheet.childrenNames = childSheets.map { it.name }
                        }
                    }
            } else {
                spreadSheetDao.fetchAll(parentUid)
                    .map { it.fromEntity() }
            })
        }

    suspend fun updateSheetName(spreadSheet: SpreadSheet) {
        withContext(Dispatchers.IO) {
            spreadSheetDao.updateName(spreadSheet.uid, spreadSheet.name, spreadSheet.parentName)
        }
    }

    suspend fun deleteSheet(spreadSheet: SpreadSheet) {
        withContext(Dispatchers.IO) {
            // Delete main spreadsheet
            spreadSheetDao.deleteByUid(spreadSheet.uid)
            cellDao.deleteBySheetUid(spreadSheet.uid)

            // Delete child spreadsheets
            val childSheets = spreadSheetDao.fetchAll(parentUid = spreadSheet.uid)
            childSheets.forEach { childSheet ->
                spreadSheetDao.deleteByUid(childSheet.uid)
                cellDao.deleteBySheetUid(childSheet.uid)
            }
        }
    }

    suspend fun deleteRow(row: Int, sheetUid: String) {
        withContext(Dispatchers.IO) {
            cellDao.deleteRow(row, sheetUid)
            // Shift rows up
            cellDao
                .fetch(sheetUid)
                .filter { cell -> cell.rowIndex > row }
                .map { cell -> cell.copy(rowIndex = cell.rowIndex - 1) }
                .forEach { cellDao.update(it) }
        }
    }

    suspend fun deleteColumn(column: Int, sheetUid: String) {
        withContext(Dispatchers.IO) {
            cellDao.deleteColumn(column, sheetUid)
            // Shift columns left
            cellDao
                .fetch(sheetUid)
                .filter { it.columnIndex > column }
                .map { cell -> cell.copy(columnIndex = cell.columnIndex - 1) }
                .forEach { cellDao.update(it) }
        }
    }

    suspend fun addRow(row: Int, sheetUid: String) {
        withContext(Dispatchers.IO) {
            // Get previous row to determine widths of new cells
            val baseRow = cellDao.fetch(sheetUid).filter { it.rowIndex == row - 1 }

            // Shift rows down
            cellDao
                .fetch(sheetUid)
                .filter { cell -> cell.rowIndex >= row }
                .sortedByDescending { it.rowIndex } // Start from the bottom to avoid overwriting
                .map { cell -> cell.copy(rowIndex = cell.rowIndex + 1) }
                .forEach { cellDao.update(it) }

            // Add empty row
            val maxColumnIndex = cellDao.fetch(sheetUid).maxOfOrNull { it.columnIndex } ?: 0
            (0..maxColumnIndex)
                .map { columnIndex ->
                    val existingCell = baseRow
                        .find { it.columnIndex == columnIndex }
                    createCell(
                        sheetUid,
                        row,
                        columnIndex,
                        existingCell?.style ?: "",
                        existingCell?.width ?: DEFAULT_WIDTH
                    )
                }
                .forEach { newCell -> cellDao.add(newCell) }
        }
    }

    suspend fun addColumn(column: Int, sheetUid: String) {
        withContext(Dispatchers.IO) {
            // Shift columns right
            val cells = cellDao.fetch(sheetUid)
            cells
                .filter { it.columnIndex >= column }
                .sortedByDescending { it.columnIndex } // Start from the right to avoid overwriting
                .map { cell -> cell.copy(columnIndex = cell.columnIndex + 1) }
                .forEach { cellDao.update(it) }

            // Add empty column
            val maxRowIndex = cells.maxOfOrNull { it.rowIndex } ?: 0
            (0..maxRowIndex)
                .map { rowIndex -> createCell(sheetUid, rowIndex, column) }
                .forEach { newCell -> cellDao.add(newCell) }
        }
    }

    suspend fun changeColumnWidth(
        column: Int,
        newWidth: Int,
        newAlign: Style.Align,
        sheetUid: String
    ) {
        withContext(Dispatchers.IO) {
            cellDao
                .fetch(sheetUid)
                .filter { it.columnIndex == column }
                .map { cell ->
                    val style = cell.fromEntity().parsedStyle
                    cell.copy(width = newWidth, style = style.copy(align = newAlign).toJson())
                }
                .forEach { cellDao.update(it) }
        }
    }

    suspend fun updateParentUpdated(uid: String) {
        spreadSheetDao.setUpdatedTimestamp(uid)
    }

    private fun createCell(
        sheetUid: String,
        row: Int,
        columnIndex: Int,
        style: String = "",
        width: Int = DEFAULT_WIDTH
    ): CellDto = CellDto(
        sheetUid = sheetUid,
        rowIndex = row,
        columnIndex = columnIndex,
        value = "",
        formula = "",
        style = style,
        width = width
    )

    companion object {
        const val DEFAULT_WIDTH = 80
    }
}
