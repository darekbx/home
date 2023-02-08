package com.darekbx.storage.lifetimememo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
class LocationDto(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "memo_id") val memoId: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double
)