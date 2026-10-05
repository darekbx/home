package com.darekbx.notebookcheckreader.domain

import com.darekbx.storage.notebookcheckreader.RssDao

class DeleteItemUseCase(
    private val rssDao: RssDao
) {

    suspend operator fun invoke(id: String) {
        rssDao.delete(id)
    }
}
