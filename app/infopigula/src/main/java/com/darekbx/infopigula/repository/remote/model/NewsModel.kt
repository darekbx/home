package com.darekbx.infopigula.repository.remote.model

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    val pager: Pager,
    @SerializedName("form_options")
    val formOptions: FormOptions,
    val rows: List<Row>
)

data class Pager(
    @SerializedName("items_per_page")
    val itemsPerPage: Int,
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_items")
    val totalItems: String
)

data class FormOptions(
    val groups: List<Group>,
    @SerializedName("last_releases")
    val lastReleases: List<LastRelease>
)

data class Group(
    @SerializedName("target_id")
    val targetId: String,
    val value: String,
    val type: String,
    val access: String
)

data class LastRelease(
    @SerializedName("target_id")
    val targetId: String,
    val value: String
)

data class Row(
    val nid: Int,
    @SerializedName("group_target_id")
    val groupTargetId: Int,
    @SerializedName("title_news")
    val titleNews: String,
    @SerializedName("title_release")
    val titleRelease: Int,
    val flagged: Int,
    @SerializedName("field_special_news")
    val fieldSpecialNews: Int,
    @SerializedName("field_news_positive")
    val fieldNewsPositive: Int,
    @SerializedName("creator_logo_img")
    val creatorLogoImg: String,
    @SerializedName("creator_external_link")
    val creatorExternalLink: String,
    @SerializedName("source_logo_img")
    val sourceLogoImg: String,
    @SerializedName("source_external_link")
    val sourceExternalLink: String,
    @SerializedName("source_external_link_override")
    val sourceExternalLinkOverride: String,
    @SerializedName("field_news_content")
    val fieldNewsContent: String,
    @SerializedName("release_date_range")
    val releaseDateRange: String,
    @SerializedName("field_publication_date")
    val fieldPublicationDate: String,
    val delta: Int,
    @SerializedName("field_multimedia_slider")
    val fieldMultimediaSlider: String,
    @SerializedName("youtube_video")
    val youtubeVideo: String,
    val vote: Vote
)

data class Vote(
    @SerializedName("user_vote")
    val userVote: Boolean,
    @SerializedName("user_stars")
    val userStars: Any?,
    @SerializedName("vote_count")
    val voteCount: String,
    @SerializedName("vote_average")
    val voteAverage: Double
)
