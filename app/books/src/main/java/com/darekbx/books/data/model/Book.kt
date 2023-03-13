package com.darekbx.books.data.model

import androidx.compose.runtime.MutableState

object Flags {

    val KINDLE = "0"
    val GOOD = "1"
    val BEST = "2"
    val IS_ENGLISH = "3"

    fun computeFlags(
        kindle: MutableState<Boolean>,
        good: MutableState<Boolean>,
        best: MutableState<Boolean>,
        english: MutableState<Boolean>
    ): String {
        var flags = ""
        if (kindle.value) flags += KINDLE
        if (good.value) flags += GOOD
        if (best.value) flags += BEST
        if (english.value) flags += IS_ENGLISH
        return flags
    }
}

data class Book(
    var id: Long?,
    val author: String,
    val title: String,
    val flags: String,
    var year: Int = -1
) {

    fun isFromKindle() = flags.contains("0")

    fun isGood() = flags.contains("1")

    fun isBest() = flags.contains("2")

    fun isInEnglish() = flags.contains("3")
}
