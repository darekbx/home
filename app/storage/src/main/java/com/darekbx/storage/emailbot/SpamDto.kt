package com.darekbx.storage.emailbot

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "spam_item")
data class SpamDto(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val from: String?,
    val subject: String?,
)
