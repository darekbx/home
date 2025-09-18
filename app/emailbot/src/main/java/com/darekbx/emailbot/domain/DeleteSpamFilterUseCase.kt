package com.darekbx.emailbot.domain

import com.darekbx.storage.emailbot.SpamDao

class DeleteSpamFilterUseCase(private val spamDao: SpamDao) {

    suspend operator fun invoke(id: String) {
        spamDao.delete(id)
    }
}
