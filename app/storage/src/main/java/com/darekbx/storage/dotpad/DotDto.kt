package com.darekbx.dotpad.repository.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "dots", indices = arrayOf(Index(value = ["is_archived"])))
class DotDto(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "text") var text: String = "",
    @ColumnInfo(name = "size") var size: Int = 0,
    @ColumnInfo(name = "color") var color: Int = 0,
    @ColumnInfo(name = "position_x") var positionX: Int = 0,
    @ColumnInfo(name = "position_y") var positionY: Int = 0,
    @ColumnInfo(name = "created_date") var createdDate: Long = 0,
    @ColumnInfo(name = "is_archived") var isArchived: Boolean = false,
    @ColumnInfo(name = "is_sticked") var isSticked: Boolean = false,
    @ColumnInfo(name = "reminder") var reminder: Long? = null,
    @ColumnInfo(name = "calendar_event_id") var calendarEventId: Long? = null,
    @ColumnInfo(name = "calendar_reminder_id") var calendarReminderId: Long? = null
)