package com.darekbx.storage.diggpl

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_entry")
class SavedEntryDto(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "entry_id") val entryId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String
)
