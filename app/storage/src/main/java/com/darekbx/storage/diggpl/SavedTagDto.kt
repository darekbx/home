package com.darekbx.storage.diggpl

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_tag")
class SavedTagDto(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "last_date") val lastDate: String
)
