package com.darekbx.emailbot.repository.storage

import android.content.Context
import android.content.SharedPreferences

class CommonPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("email_bot_prefs", Context.MODE_PRIVATE)

    private val removedSpamCountKey = "removedSpamCount"

    fun incrementRemovedSpamCount(value: Int): Int {
        val current = prefs.getInt(removedSpamCountKey, 137) /* actual value */
        val updated = current + value

        prefs.edit()
            .putInt(removedSpamCountKey, updated)
            .commit()

        return updated
    }
}