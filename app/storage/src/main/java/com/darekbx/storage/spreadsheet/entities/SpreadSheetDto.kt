package com.darekbx.storage.spreadsheet.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "spread_sheet")
data class SpreadSheetDto(
    @PrimaryKey val uid: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "parent_name") val parentName: String,
    @ColumnInfo(name = "parent_uid") val parentUid: String? = null,
    @ColumnInfo(name = "created_timestamp") val createdTimestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_timestamp") val updatedTimestamp: Long = System.currentTimeMillis()
)
