package com.darekbx.storage.weight

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_entry")
class WeightDto(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "weight") val weight: Double,
    @ColumnInfo(name = "type") val type: Int
)
