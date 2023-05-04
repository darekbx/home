package com.darekbx.rssreader.data

import com.darekbx.rssreader.data.model.NewsItem
import com.prof.rssparser.Parser
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class NewsRepository @Inject constructor(private val parser: Parser) {

    suspend fun loadAll( notifyProgress: suspend (String, Double) -> Unit): List<NewsItem> {
        val items = mutableListOf<NewsItem>()
        val sources = getSources()
        var progress = 0.0

        getSources().forEach { source ->
            val progressPercent = (++progress / sources.size) * 100.0
            notifyProgress(source.name, progressPercent)
            val sourceItems = loadSource(source)
            items.addAll(sourceItems)
        }

        return items.sortedByDescending { it.date }
    }

    private suspend fun loadSource(newsSource: NewsSource): List<NewsItem> {
        val channel = parser.getChannel(newsSource.url)
        return channel.articles.map { article ->
            val date = rssFormat.parse(article.pubDate)
            NewsItem(
                newsSource.icon,
                article.link,
                article.image,
                article.title,
                article.description,
                date
            ).apply {
                formattedDate = parsedFormat.format(date)
            }
        }
    }

    private val rssFormat by lazy { SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.getDefault()) }
    private val parsedFormat by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    private fun getSources() = listOf(
        NewsSource.TuStolicaBemowo(),
        NewsSource.TuStolicaWola(),
        NewsSource.TuStolicaMokotow(),
        NewsSource.TuStolicaBielany(),
        NewsSource.TuStolicaWawer(),
        NewsSource.TuStolicaBialoleka(),
        NewsSource.TuStolicaPlus(),
        NewsSource.YCombinator(),
        NewsSource.Hackaday(),
        NewsSource.Arduino(),
        //NewsSource.Niebezpiecznik(),
        //NewsSource.Forbot()
    )
}
