package com.darekbx.notebookcheckreader.repository.remote

import com.darekbx.notebookcheckreader.model.RssItem
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

class RssFetch(
    private val client: HttpClient,
    private val rssParser: RssParser
) {

    suspend fun fetch(url: String): List<RssItem> {
        val response: HttpResponse = client.get(url) {
            headers {
                append("User-Agent", "Mozilla/5.0")
                append("Accept", "application/rss+xml, application/xml")
            }
        }
        val body = response.bodyAsText()
        return rssParser.parse(body)
    }
}
