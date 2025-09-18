package com.darekbx.notebookcheckreader.domain

import com.darekbx.notebookcheckreader.model.RssItem
import com.darekbx.notebookcheckreader.repository.toModel
import com.darekbx.storage.notebookcheckreader.RssDao
import com.darekbx.storage.notebookcheckreader.RssFavouritesDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FetchRssItemsUseCase(
    private val rssDao: RssDao,
    private val favouritesDao: RssFavouritesDao
) {

    operator fun invoke(): Flow<List<RssItem>> {
        return rssDao.fetch().map { list ->
            val favouriteItems = favouritesDao.fetch()
            list.map { item ->
                item.toModel().apply {
                    isFavourite = favouriteItems.any { favouriteItem ->
                        favouriteItem.itemId == this.localId
                    }
                }
            }
        }
    }
}
