package com.darekbx.storage.fuel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuel_entry")
class FuelEntryDto(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "liters") val liters: Double = 0.0,
    @ColumnInfo(name = "cost") val cost: Double = 0.0,
    @ColumnInfo(name = "type") val type: Int = 0
)
