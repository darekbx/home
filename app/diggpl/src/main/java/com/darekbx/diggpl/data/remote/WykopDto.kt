package com.darekbx.diggpl.data.remote

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName

enum class ResourceType(val type: String) {
    LINK("link"),
    ENTRY("entry"),
    ENTRY_COMMENT("entry_comment"),
}

sealed class ResponseResult<out H> {
    data class Success<out T>(val data: T) : ResponseResult<T>()
    data class Failure(val error: Throwable) : ResponseResult<Nothing>()
}

data class DataWrapper<T>(val data: T)

data class ListWrapper<T>(val data: T, val pagination: Pagination)

data class Pagination(val total: Int)

data class Data(val key: String, val secret: String)

data class AuthToken(val token: String)

data class TagNewCount(val count: Int)

data class TagAutocomplete(val name: String)

data class StreamItem(
    val id: Int,
    val title: String,
    @SerializedName("created_at")
    val date: String,
    val slug: String,
    val description: String,
    val content: String,
    val source: Source?,
    val author: Author,
    val comments: Comments,
    val votes: Votes,
    val tags: List<String>,
    val adult: Boolean,
    val hot: Boolean,
    val media: Media,
    val resource: String
)

data class Media(
    val photo: MediaPhoto?,
    val embed: MediaEmbed?,
    val survey: Survey?
)

data class Survey(
    val question: String,
    val count: Int,
    val answers: List<SurveyAnswer>
)

data class SurveyAnswer(
    val text: String,
    val count: Int
)

data class MediaPhoto(
    val label: String,
    val url: String,
    @SerializedName("mime_type")
    val mimeType: String
)

data class MediaEmbed(
    val thumbnail: String?,
    val url: String?
)

data class Comments(
    val count: Int
)

data class Votes(
    val up: Int,
    val down: Int
) {
    fun summary() = up - down
}

data class Source(
    val label: String,
    val url: String
)

data class Author(
    val username: String,
    val avatar: String,
    val gender: String?,
    val color: String?
) {
    fun validColor(): Color {
        return when (color) {
            "orange" -> Color(236, 101, 50)
            "green" -> Color(80, 148, 64)
            "burgundy" -> Color(178, 67, 62)
            else -> Color(210, 210, 210)
        }
    }
}

data class Comment(
    val id: Int,
    @SerializedName("created_at")
    val date: String,
    val content: String,
    val author: Author,
    val votes: Votes,
    val tags: List<String>,
    val adult: Boolean,
    val media: Media
)

data class Related(
    @SerializedName("created_at")
    val date: String,
    val title: String,
    val author: Author,
    val source: Source?,
    val votes: Votes,
    val adult: Boolean,
    val media: Media
)
