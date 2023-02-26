package com.darekbx.diggpl.data.local.model

data class SavedTag(
    val name: String,
    var lastDate: String,
    var newEntriesCount: Int = 0
) {
    fun hasNewEntries() = newEntriesCount > 0

    override fun toString(): String {
        return "$name, $lastDate, $newEntriesCount"
    }
}