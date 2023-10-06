package com.darekbx.infopigula.repository.remote.model

import com.google.gson.annotations.SerializedName

/**
 * User login post data body
 */
data class UserLogin(
    val name: String,
    val pass: String
)

data class LoginResponse(
    @SerializedName("current_user")
    val currentUser: CurrentUser,
    @SerializedName("csrf_token")
    val csrfToken: String,
    @SerializedName("logout_token")
    val logoutToken: String,
    @SerializedName("access_token")
    val accessToken: String
)

data class CurrentUser(
    val uid: String,
    val name: String
)
