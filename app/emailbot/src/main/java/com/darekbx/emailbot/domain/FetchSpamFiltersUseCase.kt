package com.darekbx.emailbot.domain

import com.darekbx.storage.emailbot.SpamDao
import com.darekbx.storage.emailbot.SpamDto

class FetchSpamFiltersUseCase(private val spamDao: SpamDao) {

    suspend operator fun invoke(): List<SpamDto> {
        return spamDao.getAll()
    }
}
