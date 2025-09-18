package com.darekbx.storage.notebookcheckreader

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "rss_favourite_items")
data class RssFavouriteItemDto(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val itemId: String
)
