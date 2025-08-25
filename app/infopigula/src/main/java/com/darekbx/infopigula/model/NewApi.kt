package com.darekbx.infopigula.model

data class NewsResponse(
    val title: String,
    val publishDate: String,
    val categories: List<Category>
)

data class Category(
    val name: String,
    val priority: Int,
    val news: List<SingleNews>
)

data class SingleNews(
    val id: String,
    val content: String,
    val type: String,
    val premiumNews: Boolean,
    val order: Int,
    val images: List<String>,
    val movie: String?,
    val rating: Double,
    val totalVotes: Int,
    val source: Source,
    val userVote: Int?,
    val isFavourite: Boolean?
)

data class Source(
    val image: String,
    val name: String,
    val url: String
)
