package com.darekbx.lifetimememo.screens.memos.model

import com.darekbx.lifetimememo.data.dto.ContainerDto

class Container(
    val uid: String,
    val parentUid: String? = null,
    val title: String,
    val subtitle: String? = null,
    val timestamp: Long = 0L
) {
    var childrenCount = 0

    companion object {
        fun ContainerDto.toDomain() = Container(
            uid,
            parentUid,
            title,
            subtitle,
            timestamp
        )
    }
}