package com.darekbx.spreadsheet.domain

import android.content.ContentResolver
import android.net.Uri
import com.darekbx.spreadsheet.domain.SpreadSheetUseCases.Companion.DEFAULT_WIDTH
import com.darekbx.storage.spreadsheet.CellDao
import com.darekbx.storage.spreadsheet.SpreadSheetDao
import com.darekbx.storage.spreadsheet.entities.CellDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ImportUseCase(
    private val spreadSheetDao: SpreadSheetDao,
    private val cellDao: CellDao,
    private val contentResolver: ContentResolver
) {
    suspend fun import(spreadSheetUid: String, name: String, parentName: String, uri: Uri) {
        // 1. Delete cells from sheet
        cellDao.deleteBySheetUid(spreadSheetUid)

        // 2. Read file
        val parts = readFile(uri)
            ?: throw IllegalArgumentException("File is empty or cannot be read")

        // 3. Update sheet name
        spreadSheetDao.updateName(spreadSheetUid, name, parentName)

        // 4. Create cells
        val cells = createEntities(parts, spreadSheetUid)
        cellDao.add(cells)

        // 5. Add some delay
        delay(500)

        // 6. Update updated timestamp
        upadteParentTimestamp(spreadSheetUid)
    }

    private fun createEntities(
        parts: List<List<String>>,
        spreadSheetUid: String
    ): List<CellDto> = parts.flatMapIndexed { rowIndex, row ->
        row.mapIndexed { columnIndex, value ->
            CellDto(
                sheetUid = spreadSheetUid,
                rowIndex = rowIndex,
                columnIndex = columnIndex,
                value = value,
                formula = "",
                style = "",
                width = DEFAULT_WIDTH
            )
        }
    }

    private fun readFile(uri: Uri): List<List<String>>? {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader().use { reader ->
                return reader.lineSequence()
                    .map { line -> line.split(",") }
                    .toList()
            }
        }
        return null
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
