package com.darekbx.storage.favourites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_item")
data class FavouriteItemDto(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "rating") val rating: Float,
    @ColumnInfo(name = "comment") val comment: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
)