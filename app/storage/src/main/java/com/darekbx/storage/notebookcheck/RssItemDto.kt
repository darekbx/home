package com.darekbx.storage.notebookcheckreader

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "rss_item")
data class RssItemDto(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val link: String,
    val description: String,
    val category: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    val enclosure: String,
    val isRead: Boolean = false,
)
