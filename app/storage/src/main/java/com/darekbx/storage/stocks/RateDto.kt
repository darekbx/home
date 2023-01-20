package com.darekbx.storage.stocks

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rate")
class RateDto(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "currency_id") val currencyId: Long,
    @ColumnInfo(name = "value") val value: Double
)