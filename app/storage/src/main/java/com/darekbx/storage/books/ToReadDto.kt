package com.darekbx.storage.books

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_to_read")
class ToReadDto(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "title") val title: String
)
