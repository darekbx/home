package com.darekbx.infopigula.model

data class News(
    val title: String,
    val sourceLogo: String,
    val externalLink: String,
    val externalLinkTitle: String,
    val content: String,
    val voteCount: String,
    val voteScore: String
)