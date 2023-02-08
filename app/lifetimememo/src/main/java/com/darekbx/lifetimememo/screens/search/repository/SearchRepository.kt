package com.darekbx.lifetimememo.screens.search.repository

import com.darekbx.storage.lifetimememo.MemoDao
import com.darekbx.lifetimememo.screens.memos.model.Container
import com.darekbx.lifetimememo.screens.memos.model.Container.Companion.toDomain
import com.darekbx.lifetimememo.screens.memos.model.Memo
import com.darekbx.lifetimememo.screens.memos.model.Memo.Companion.toDomain
import com.darekbx.storage.lifetimememo.SearchDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val searchDao: SearchDao,
    private val memoDao: MemoDao
) {

    fun searchInMemos(query: String): Flow<List<Memo>> {
        return searchDao.searchMemos(query).map { memoDtos ->
            memoDtos.map {
                val memo = it.toDomain()
                memo.hasLocation = memoDao.countLocations(memo.uid) > 0
                memo.categoryName = memoDao.getCategory(memo.categoryUid).name
                memo
            }
        }
    }

    fun searchInContainers(query: String): Flow<List<Container>> {
        return searchDao.searchContainers(query).map { containerDtos ->
            containerDtos.map {
                val container = it.toDomain()
                container.childrenCount = countChildren(it.uid)
                container
            }
        }
    }

    private suspend fun countChildren(containerUid: String): Int {
        val childContainersCount = memoDao.countContainers(containerUid)
        val memosCount = memoDao.countMemos(containerUid)
        return childContainersCount + memosCount
    }
}
