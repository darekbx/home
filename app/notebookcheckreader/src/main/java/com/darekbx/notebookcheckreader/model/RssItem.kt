package com.darekbx.notebookcheckreader.model

data class RssItem(
    val title: String,
    val link: String,
    val description: String,
    val category: String,
    val timestamp: Long,
    val enclosure: String
) {
    var localId: String = ""
    var isRead: Boolean = false
    var isFavourite: Boolean = false
}
