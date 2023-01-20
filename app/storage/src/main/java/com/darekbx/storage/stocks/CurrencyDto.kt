package com.darekbx.storage.stocks

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
class CurrencyDto(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "label") val label: String,
    @ColumnInfo(name = "query_param") val queryParam: String // e.g.: usdpln
) {
    override fun toString(): String {
        return "$id, $label, $queryParam"
    }
}