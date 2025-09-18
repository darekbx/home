package com.darekbx.notebookcheckreader.domain

import com.darekbx.storage.notebookcheckreader.RssDao
import kotlinx.coroutines.flow.Flow

class FetchItemsCountUseCase(
    private val rssDao: RssDao
) {
    operator fun invoke(): Flow<Int> {
        return rssDao.fetchCount()
    }
}
