package com.darekbx.notebookcheckreader.domain

import com.darekbx.storage.notebookcheckreader.RssDao
import com.darekbx.storage.notebookcheckreader.RssFavouritesDao

class DeleteOldItemsUseCase(
    private val rssDao: RssDao,
    private val favouritesDao: RssFavouritesDao
) {

    suspend operator fun invoke() {
        val count = rssDao.fetchCountSync()
        val favoriteIds = favouritesDao.fetch().map { it.itemId }
        if (count > MAX_ITEMS) {
            rssDao.deleteOldest(count - MAX_ITEMS, favoriteIds)
        }
    }

    companion object {
        private const val MAX_ITEMS = 50
    }
}
