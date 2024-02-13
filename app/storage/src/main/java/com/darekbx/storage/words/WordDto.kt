package com.darekbx.storage.words

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_item")
data class WordDto(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "translation") val translation: String,
    @ColumnInfo(name = "checked_count") val checkedCount: Int,
    @ColumnInfo(name = "is_archived") val isArchived: Boolean,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
)