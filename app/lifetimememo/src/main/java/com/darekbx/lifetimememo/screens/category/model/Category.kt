package com.darekbx.lifetimememo.screens.category.model

import com.darekbx.storage.lifetimememo.CategoryDto

class Category(
    val uid: String,
    val name: String
) {

    var entriesCount = 0

    fun hasNoRelatedEntries(): Boolean = entriesCount == 0

    companion object {
        fun CategoryDto.toDomain() = Category(uid, name)
    }
}