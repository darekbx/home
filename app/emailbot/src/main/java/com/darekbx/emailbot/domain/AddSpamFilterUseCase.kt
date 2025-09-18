package com.darekbx.emailbot.domain

import com.darekbx.storage.emailbot.SpamDao
import com.darekbx.storage.emailbot.SpamDto

class AddSpamFilterUseCase(private val spamDao: SpamDao) {

    suspend operator fun invoke(from: String?, subject: String?) {
        if (from.isNullOrBlank() && subject.isNullOrBlank()) {
            return
        }
        spamDao.insert(SpamDto(from = from, subject = subject))
    }
}
