package com.darekbx.spreadsheet.ui.grid.viewmodel

import com.darekbx.spreadsheet.BuildConfig
import com.darekbx.spreadsheet.domain.CellUseCases
import com.darekbx.spreadsheet.model.Cell

class CellsLoader(private val cellUseCases: CellUseCases) {

    suspend fun loadCells(spreadSheetUid: String): List<Cell> {
        return try {
            cellUseCases.fetchCells(spreadSheetUid)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            emptyList()
        }
    }
}