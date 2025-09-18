package com.darekbx.notebookcheckreader.domain

import com.darekbx.storage.notebookcheckreader.RssFavouriteItemDto
import com.darekbx.storage.notebookcheckreader.RssFavouritesDao

class AddRemoveToFavouritesUseCase(private val favouritesDao: RssFavouritesDao) {

    suspend operator fun invoke(rssItemId: String) {
        favouritesDao.getById(rssItemId)
            ?.let { favouritesDao.delete(rssItemId) }
            ?: run { favouritesDao.add(RssFavouriteItemDto(itemId = rssItemId)) }
    }
}
