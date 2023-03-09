package com.darekbx.rssreader.data.model

import androidx.annotation.DrawableRes
import java.util.Date

data class NewsItem(
    @DrawableRes val iconId: Int,
    val url: String?,
    val imageUrl: String?,
    val title: String?,
    val description: String?,
    val date: Date?
) {
    var formattedDate: String = ""
}
