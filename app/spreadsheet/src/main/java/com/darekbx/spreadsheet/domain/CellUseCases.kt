package com.darekbx.spreadsheet.domain

import com.darekbx.spreadsheet.model.Cell
import com.darekbx.spreadsheet.model.Cell.Companion.fromEntity
import com.darekbx.spreadsheet.repository.SettingsRepository
import com.darekbx.storage.spreadsheet.CellDao
import com.darekbx.storage.spreadsheet.SpreadSheetDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CellUseCases(
    private val cellDao: CellDao,
    private val spreadSheetDao: SpreadSheetDao,
    private val settingsRepository: SettingsRepository
) {

    suspend fun fetchCells(sheetUid: String): List<Cell> =
        withContext(Dispatchers.IO) {
            cellDao.fetch(sheetUid)
                .map { it.fromEntity() }
        }

    suspend fun fetchCells(sheetUid: String, columnIndex: Int, rows: List<Int>): List<Cell> =
        withContext(Dispatchers.IO) {
            val cells = cellDao.fetch(sheetUid)
                .map { it.fromEntity() }
            cells.filter { it.columnIndex == columnIndex && rows.contains(it.rowIndex + 1) }
        }

    suspend fun updateCell(cell: Cell) {
        withContext(Dispatchers.IO) {
            upadteParentTimestamp(cell.sheetUid)
            cellDao.update(
                cell.sheetUid,
                cell.rowIndex,
                cell.columnIndex,
                cell.value,
                cell.formula,
                cell.style,
                cell.width
            )
            settingsRepository.increaseLocalVersion()
        }
    }

    private suspend fun upadteParentTimestamp(sheetUid: String) {
        withContext(Dispatchers.IO) {
            val spreadSheet = spreadSheetDao.fetch(sheetUid)
            spreadSheet?.parentUid
                ?.let { spreadSheetDao.setUpdatedTimestamp(it) }
                ?: run { spreadSheetDao.setUpdatedTimestamp(sheetUid) }
        }
    }
}
