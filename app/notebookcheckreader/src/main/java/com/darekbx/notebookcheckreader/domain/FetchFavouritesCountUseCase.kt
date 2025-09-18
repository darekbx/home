package com.darekbx.notebookcheckreader.domain

import com.darekbx.storage.notebookcheckreader.RssFavouritesDao
import kotlinx.coroutines.flow.Flow

class FetchFavouritesCountUseCase(
    private val favouritesDao: RssFavouritesDao
) {
    operator fun invoke(): Flow<Int> {
        return favouritesDao.fetchCount()
    }
}
