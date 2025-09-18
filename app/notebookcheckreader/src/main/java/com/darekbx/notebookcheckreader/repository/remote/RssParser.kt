package com.darekbx.notebookcheckreader.repository.remote

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

import com.darekbx.notebookcheckreader.model.RssItem
import com.darekbx.notebookcheckreader.repository.DateTimeParser

class RssParser {

    fun parse(rssXml: String): List<RssItem> {
        val items = mutableListOf<RssItem>()

        val factory = XmlPullParserFactory.newInstance().apply {
            isNamespaceAware = false
        }

        val parser = factory.newPullParser().apply {
            setInput(rssXml.reader())
        }

        var currentItem = RssItemBuilder()
        var inItem = false
        var text = ""

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name.lowercase()) {
                        "item" -> {
                            inItem = true
                            currentItem = RssItemBuilder()
                        }
                        "enclosure" -> {
                            if (inItem) {
                                currentItem.enclosure = parser.getAttributeValue(null, "url") ?: ""
                            }
                        }
                    }
                }

                XmlPullParser.TEXT -> {
                    text = parser.text ?: ""
                }

                XmlPullParser.END_TAG -> {
                    if (inItem) {
                        when (parser.name.lowercase()) {
                            "title" -> currentItem.title = text
                            "link" -> currentItem.link = text
                            "description" -> currentItem.description = text
                            "category" -> currentItem.category = text
                            "pubdate" -> currentItem.pubDate = text
                            "item" -> {
                                items.add(currentItem.build())
                                inItem = false
                            }
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        return items
    }

    private class RssItemBuilder {
        var title: String = ""
        var link: String = ""
        var description: String = ""
        var category: String = ""
        var pubDate: String = ""
        var enclosure: String = ""

        fun build(): RssItem = RssItem(
            title = title,
            link = link,
            description = description,
            category = category,
            timestamp = DateTimeParser.getTimestamp(this.pubDate),
            enclosure = enclosure
        )
    }
}
