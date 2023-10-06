package com.darekbx.infopigula.model

data class User(
    val id: String,
    val name: String,
    val subscriptionPlanName: String,
    val subscriptionEnd: String,
    val image: String? = null
)