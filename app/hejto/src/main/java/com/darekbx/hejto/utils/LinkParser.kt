package com.darekbx.hejto.utils

import com.darekbx.hejto.BuildConfig

object LinkParser {

    class Link(val source: String, val label: String, val url: String) {
        var start: Int = 0
        var end: Int = 0

        override fun toString(): String {
            return "($start -> $end) $label, $url"
        }
    }

    /**
     * Extracts links from the text
     */
    fun extractLinks(content: String): List<Link> {
        return Regex(
            pattern = "\\[[^]]*]\\([^)]*\\)",
            options = setOf(RegexOption.DOT_MATCHES_ALL)
        )
            .findAll(content)
            .mapNotNull {
                try {
                    parseLink(it.value).apply {
                        start = content.indexOf(it.value, it.range.start)
                        end = start + it.value.length
                    }
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace()
                    }
                    null
                }
            }
            .toList()
    }

    /**
     * Parses [Klaudiusz](https://imperiumromanum.pl/biografie/klaudiusz/) to:
     * label: Klaudiusz
     * url: https://imperiumromanum.pl/biografie/klaudiusz/
     */
    fun parseLink(link: String): Link {
        val label = link.substring(1, link.indexOf("]"))
        val url = link.substring(link.indexOf("(") + 1, link.indexOf(")"))
        return Link(link, label, url)
    }
}
