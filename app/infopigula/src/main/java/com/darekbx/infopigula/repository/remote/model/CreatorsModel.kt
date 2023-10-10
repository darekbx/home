package com.darekbx.infopigula.repository.remote.model

import com.google.gson.annotations.SerializedName

data class Creator(
    val name: String,
    val tid: String,
    val description: String,
    @SerializedName("external_link")
    val externalLink: String,
    val flagged: String,
    val recommended: String,
    val logo: String
)
