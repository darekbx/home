package com.darekbx.hejto.data.local.model

data class FavouriteTag(
    val name: String,
    val entriesCount: Int,
    var newEntriesCount: Int = 0
) {
    fun hasNewEntries() = newEntriesCount > 0
}