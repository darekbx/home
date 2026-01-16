package com.darekbx.emailbot.bot

import android.util.Log
import com.darekbx.emailbot.imap.EmailOperations
import com.darekbx.emailbot.imap.FetchEmails
import com.darekbx.emailbot.model.Email
import com.darekbx.emailbot.repository.RefreshBus
import com.darekbx.emailbot.repository.storage.CommonPreferences
import com.darekbx.storage.emailbot.SpamDao
import com.darekbx.storage.emailbot.SpamDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.forEach
import kotlin.text.contains

class CleanUpBot(
    private val fetchEmails: FetchEmails,
    private val emailOperations: EmailOperations,
    private val spamDao: SpamDao,
    private val refreshBus: RefreshBus,
    private val commonsPreferences: CommonPreferences
) {
    data class Result(
        val removedCount: Int,
        val messagesCount: Int,
        val totalRemovedCount: Int,
        val keysCount: Int
    )

    suspend fun cleanUp() : Result {
        return withContext(Dispatchers.IO) {
            // 1. Fetch spam filters
            val spamFilters = spamDao.getAll()
                .also { Log.d("CleanUpBot", "Fetched ${it.size} filters") }

            // 2. Fetch emails
            val emails = fetchEmails.fetch()
                .also { Log.d("CleanUpBot", "Fetched ${it.size} emails") }

            // 3. Mark spam emails
            emails.markSpam(spamFilters)

            // 4. Get spam emails message ids
            val spamMessageNumbers = emails
                .filter { it.isSpam }
                .mapNotNull { it.messageNumber }
                .also { Log.d("CleanUpBot", "Found ${it.size} spam emails") }

            // 5. Delete spam emails
            val removedCount = emailOperations.removeEmail(*spamMessageNumbers.toIntArray())
                .also { Log.d("CleanUpBot", "Removed $it spam emails") }

            // 6. Notify about refresh
            refreshBus.publishChanges()

            // 7. Increment removed spam count in preferences
            val (totalRemovedCount, keysCount) = commonsPreferences.incrementRemovedSpamCount(removedCount)

            Log.d("CleanUpBot", "Total removed count from prefs: $totalRemovedCount")

            Result(removedCount, emails.size - removedCount, totalRemovedCount, keysCount)
        }
    }
}

fun List<Email>.markSpam(spamFilters: List<SpamDto>) {
    forEach { email ->
        email.isSpam = spamFilters.any { filter ->
            val fromFilter = filter.from
            val subjectFilter = filter.subject
            (fromFilter != null && email.from.contains(fromFilter)) ||
                    (subjectFilter != null && email.subject.contains(subjectFilter))
        }
    }
}
