package com.darekbx.infopigula.repository.remote.model

import com.google.gson.annotations.SerializedName

data class CurrentUserResponse(
    val data: UserData,
    val message: String
)

data class UserData(
    @SerializedName("uid")
    val userId: String,
    @SerializedName("user_email")
    val userEmail: String,
    @SerializedName("subscription_plan_id")
    val subscriptionPlanId: String,
    @SerializedName("subscription_plan_name")
    val subscriptionPlanName: String,
    @SerializedName("subscription_end")
    val subscriptionEnd: String
)

data class UserResponse(
    val uid: List<ValueItem>,
    val name: List<ValueItem>,
    val mail: List<ValueItem>,
    val path: List<PathItem>,
    @SerializedName("user_picture")
    val userPicture: List<String>
)

data class ValueItem(
    val value: Any
)

data class PathItem(
    val alias: String?,
    val pid: String?,
    val langcode: String
)

