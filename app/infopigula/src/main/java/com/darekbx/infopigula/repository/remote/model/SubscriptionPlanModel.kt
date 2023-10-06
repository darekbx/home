package com.darekbx.infopigula.repository.remote.model

import com.google.gson.annotations.SerializedName

data class SubscriptionPlan(
    val data: List<PlanData>
)

data class PlanData(
    val id: String,
    @SerializedName("is_free")
    val isFree: String,
    val name: String,
    val details: String,
    val amount: String,
    @SerializedName("discount_matrix")
    val discountMatrix: Map<String, Int>?
)
