package com.darekbx.hejto.data.local.model

data class FavouriteTag(
    val name: String,
    var entriesCount: Int,
    var newEntriesCount: Int = 0
) {
    fun hasNewEntries() = newEntriesCount > 0

    override fun toString(): String {
        return "$name, $entriesCount, $newEntriesCount"
    }
}