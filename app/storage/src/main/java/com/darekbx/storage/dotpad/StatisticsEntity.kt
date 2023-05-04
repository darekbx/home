package com.darekbx.dotpad.repository.local.entities

import androidx.room.ColumnInfo

class StatisticsEntity(
    @ColumnInfo(name = "occurrences") var occurrences: Int?,
    @ColumnInfo(name = "value") var value: Int?
)