package com.darekbx.timeline.model

data class Entry(
    val id: Long,
    val categoryId: Long,
    val title: String,
    val description: String,
    val timestamp: Long
)