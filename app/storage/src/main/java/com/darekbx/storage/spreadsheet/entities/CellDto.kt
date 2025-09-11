package com.darekbx.storage.spreadsheet.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "cell")
data class CellDto(
    @PrimaryKey val uid: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "sheet_uid") val sheetUid: String,
    @ColumnInfo(name = "row_index") val rowIndex: Int,
    @ColumnInfo(name = "column_index") val columnIndex: Int,
    @ColumnInfo(name = "value") val value: String,
    @ColumnInfo(name = "formula") val formula: String,
    @ColumnInfo(name = "style") val style: String,
    @ColumnInfo(name = "width") val width: Int
)
