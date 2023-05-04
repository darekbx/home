package com.darekbx.storage.legacy.model

data class LegacyDot(
    var id: Long? = null,
    var text: String = "",
    var size: Int = 0,
    var color: Int = 0,
    var positionX: Int = 0,
    var positionY: Int = 0,
    var createdDate: Long = 0,
    var isArchived: Boolean = false,
    var isSticked: Boolean = false,
    var reminder: Long? = null,
    var calendarEventId: Long? = null,
    var calendarReminderId: Long? = null
)