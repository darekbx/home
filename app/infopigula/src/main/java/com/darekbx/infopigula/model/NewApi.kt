package com.darekbx.infopigula.model

data class NewsResponse(
    val title: String,
    val categories: List<Category>
)

data class Category(
    val name: String,
    val news: List<News>
)

data class News(
    val id: String,
    val content: String,
    val images: List<String>,
    val rating: Double,
    val totalVotes: Int,
    val source: Source
)

data class Source(
    val image: String,
    val name: String,
    val url: String
)
