package com.darekbx.lifetimememo.screens.memos.model

import com.darekbx.storage.lifetimememo.LocationDto

class Location(
    val uid: String,
    val memoId: String,
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        fun LocationDto.toDomain() = Location(
            uid,
            memoId,
            latitude,
            longitude
        )
    }
}