package com.darekbx.storage.spreadsheet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.darekbx.storage.spreadsheet.entities.CellDto

@Dao
interface CellDao {

    @Query("SELECT * FROM cell WHERE uid = :cellUid")
    suspend fun getCell(cellUid: String): CellDto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(cellEntity: CellDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(cellEntity: List<CellDto>)

    @Query("SELECT * FROM cell WHERE sheet_uid = :sheetUid ORDER BY row_index, column_index")
    suspend fun fetch(sheetUid: String): List<CellDto>

    @Query("DELETE FROM cell WHERE row_index = :rowIndex AND sheet_uid = :sheetUid")
    suspend fun deleteRow(rowIndex: Int, sheetUid: String)

    @Query("DELETE FROM cell WHERE column_index = :columnIndex AND sheet_uid = :sheetUid")
    suspend fun deleteColumn(columnIndex: Int, sheetUid: String)

    @Query("""
        UPDATE 
            cell 
        SET 
            value = :value, 
            formula = :formula, 
            style = :style, 
            width = :width
        WHERE 
            sheet_uid = :sheetUid AND 
            row_index = :rowIndex AND 
            column_index = :columnIndex
    """)
    suspend fun update(
        sheetUid: String,
        rowIndex: Int,
        columnIndex: Int,
        value: String,
        formula: String? = null,
        style: String? = null,
        width: Int = DEFAULT_WIDTH
    )

    @Update
    suspend fun update(cellEntity: CellDto)

    @Query("DELETE FROM cell WHERE sheet_uid = :sheetId")
    suspend fun deleteBySheetUid(sheetId: String)

    companion object {
        const val DEFAULT_WIDTH = 80
    }
}
