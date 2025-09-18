package com.darekbx.emailbot.model

import com.darekbx.storage.emailbot.SpamDto

data class SpamFilter(
    val id: String,
    val from: String?,
    val subject: String?
) {
    companion object {
        fun toModel(dto: SpamDto): SpamFilter {
            return SpamFilter(
                id = dto.id,
                from = dto.from,
                subject = dto.subject
            )
        }
    }
}
