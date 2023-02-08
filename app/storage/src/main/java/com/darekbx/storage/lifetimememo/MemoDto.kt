package com.darekbx.storage.lifetimememo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo")
class MemoDto(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "container_uid") val parentUid: String? = null,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "category_uid") val categoryUid: String,
    @ColumnInfo(name = "subtitle") val subtitle: String? = null,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "link") val link: String? = null,
    @ColumnInfo(name = "date_time") val dateTime: Long? = null,
    @ColumnInfo(name = "flag") val flag: Int? = null,
    @ColumnInfo(name = "reminder") val reminder: Long? = null
)