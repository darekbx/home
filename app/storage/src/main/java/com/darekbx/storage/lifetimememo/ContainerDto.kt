package com.darekbx.lifetimememo.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "container")
class ContainerDto(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "parent_uid") val parentUid: String? = null,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "subtitle") val subtitle: String? = null,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
)