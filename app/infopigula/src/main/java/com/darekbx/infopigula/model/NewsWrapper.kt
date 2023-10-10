package com.darekbx.infopigula.model

data class NewsWrapper(
    val news: List<News>,
    val groups: List<Group>,
    val releases: List<LastRelease>,
    val pager: Pager
)