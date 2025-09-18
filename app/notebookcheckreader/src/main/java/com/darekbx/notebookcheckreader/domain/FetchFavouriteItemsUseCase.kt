package com.darekbx.notebookcheckreader.domain

import com.darekbx.notebookcheckreader.model.RssItem
import com.darekbx.notebookcheckreader.repository.toModel
import com.darekbx.storage.notebookcheckreader.RssDao
import com.darekbx.storage.notebookcheckreader.RssFavouritesDao

class FetchFavouriteItemsUseCase(
    private val rssDao: RssDao,
    private val favouritesDao: RssFavouritesDao
) {
    suspend operator fun invoke(): List<RssItem> {
        val favouriteItems = favouritesDao.fetch()
        if (favouriteItems.isEmpty()) {
            return emptyList()
        }
        
        val favouriteItemIds = favouriteItems.map { it.itemId }
        return rssDao.fetchByIds(favouriteItemIds).map { 
            it.toModel().apply {
                isFavourite = true
            }
        }
    }
}
