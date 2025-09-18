package com.darekbx.notebookcheckreader.repository

import com.darekbx.notebookcheckreader.model.RssItem
import com.darekbx.storage.notebookcheckreader.RssItemDto

fun RssItem.toDto() = RssItemDto(
    title = this.title,
    link = this.link,
    description = this.description,
    category = this.category,
    timestamp = this.timestamp,
    enclosure = this.enclosure,
    isRead = false
)

fun RssItemDto.toModel() = RssItem(
    title = this@toModel.title,
    link = this@toModel.link,
    description = this@toModel.description,
    category = this@toModel.category,
    timestamp = this@toModel.timestamp,
    enclosure = this@toModel.enclosure
).apply {
    localId = this@toModel.id
    isRead = this@toModel.isRead
}