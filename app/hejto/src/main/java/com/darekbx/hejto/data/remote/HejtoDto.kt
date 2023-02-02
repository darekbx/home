package com.darekbx.hejto.data.remote

import android.text.format.DateUtils
import androidx.compose.ui.graphics.Color
import com.darekbx.hejto.utils.LinkParser
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

// Format for 2023-01-25T21:40:14+01:00
val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
val displayFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

data class ResponseWrapper<T>(
    val page: Int,
    val pages: Int,
    @SerializedName("_embedded")
    val contents: Items<T>
)

data class Items<T>(
    val items: List<T>
)

data class Community(
    val name: String,
    val slug: String,
    val status: String,
    val avatar: RemoteImage?,
    @SerializedName("num_posts")
    val postsCount: Int
) {
    var previousPostsCount = 0
}

data class Tag(
    val name: String,
    @SerializedName("num_posts")
    val postsCount: Int,
    @SerializedName("num_follows")
    val followsCount: Int
) {
    var isFavourite = false
}


data class PostComment(
    val content: String,
    val author: Author,
    val images: List<RemoteImage>,
    @SerializedName("content_links")
    val contentLinks: List<ContentLink>,
    @SerializedName("num_likes")
    val likesCount: Int,
    @SerializedName("num_reports")
    val reportsCount: Int,
    @SerializedName("created_at")
    val createdAt: String
) {
    fun dateAgo(): String {
        val date = inputFormat.parse(createdAt)
        return DateUtils.getRelativeTimeSpanString(
            date.getTime(),
            Calendar.getInstance().getTimeInMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }
}

data class PostDetails(
    val type: String,
    val title: String,
    val slug: String,
    val content: String,
    val hot: Boolean,
    val images: List<RemoteImage>,
    @SerializedName("content_links")
    val contentLinks: List<ContentLink>,
    val tags: List<Tag>,
    val author: Author,
    val nsfw: Boolean,
    val controversial: Boolean,
    @SerializedName("num_likes")
    val likesCount: Int,
    @SerializedName("num_comments")
    val commentsCount: Int,
    @SerializedName("created_at")
    val createdAt: String,
    val community: Community,
    val link: String?
) {
    private val links by lazy { LinkParser.extractLinks(content) }

    val hasContentVideo = contentLinks.any { it.type == "video" }

    val cleanContent by lazy {
        var result = content
        links.forEach { link -> result = result.replace(link.source, link.label) }
        result
    }

    fun dateAgo(): String {
        val date = inputFormat.parse(createdAt)
        return DateUtils.getRelativeTimeSpanString(
            date.getTime(),
            Calendar.getInstance().getTimeInMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }

    fun displayDate(): String {
        val date = inputFormat.parse(createdAt)
        return displayFormat.format(date)
    }
}

data class Author(
    @SerializedName("username")
    val userName: String,
    @SerializedName("current_rank")
    val rank: String,
    @SerializedName("current_color")
    val rankColor: String,
    val avatar: RemoteImage?
) {
    val color = Color(android.graphics.Color.parseColor(rankColor))
}

data class RemoteImage(
    val urls: Map<String, String>?
) {
    override fun toString(): String {
        return urls
            ?.toList()
            ?.joinToString(", ") { "${it.first}: ${it.second}" } ?: ""
    }
}

data class ContentLink(val url: String, val type: String, val images: List<LinkImage>)

data class LinkImage(val url: String)