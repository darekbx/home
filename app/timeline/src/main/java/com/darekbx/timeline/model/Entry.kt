package com.darekbx.timeline.model

import java.lang.IllegalStateException

data class Entry(
    val id: Long,
    val categoryId: Long,
    val title: String,
    val description: String,
    val timestamp: Long
) {
    var category: Category? = null

    /**
     * @throws IllegalStateException when category is null
     */
    fun categoryNotNull() = category ?: throw IllegalStateException()
}