package com.darekbx.storage.legacy.model

data class LegacyTrack(
    val id: Long,
    val label: String? = null,
    val startTimestamp: Long,
    val endTimestamp: Long? = null,
    val distance: Float? = null
)
