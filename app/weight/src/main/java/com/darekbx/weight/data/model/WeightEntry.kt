package com.darekbx.weight.data.model

import java.text.SimpleDateFormat
import java.util.*

enum class EntryType(val type: Int) {
    MONIKA(1),
    DAREK(2),
    MICHAL(3)
}

data class WeightEntry(
    val id: Long?,
    val date: Long,
    val weight: Double,
    val type: EntryType
) {
    companion object {
        fun typeNameFormatted(type: EntryType) =
            type.name
                .lowercase()
                .replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                    else it.toString()
                }
    }
}

val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)