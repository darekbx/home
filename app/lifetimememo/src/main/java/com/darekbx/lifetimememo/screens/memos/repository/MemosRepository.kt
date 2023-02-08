package com.darekbx.lifetimememo.screens.memos.repository

import com.darekbx.storage.lifetimememo.MemoDao
import com.darekbx.lifetimememo.data.dto.ContainerDto
import com.darekbx.storage.lifetimememo.LocationDto
import com.darekbx.storage.lifetimememo.MemoDto
import com.darekbx.lifetimememo.screens.memos.model.Container
import com.darekbx.lifetimememo.screens.memos.model.Container.Companion.toDomain
import com.darekbx.lifetimememo.screens.memos.model.Location
import com.darekbx.lifetimememo.screens.memos.model.Location.Companion.toDomain
import com.darekbx.lifetimememo.screens.memos.model.Memo
import com.darekbx.lifetimememo.screens.memos.model.Memo.Companion.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MemosRepository @Inject constructor(
    private val memoDao: MemoDao
) : BaseMemosRepository() {

    override fun countMemos(): Flow<Int> = memoDao.countMemos()

    override fun countContainers(): Flow<Int> = memoDao.countContainers()

    suspend fun getPath(
        parentId: String?,
        path: MutableList<String> = mutableListOf()
    ): MutableList<String> {
        if (parentId != null) {
            val container = memoDao.containerSync(parentId)
            if (container != null) {
                path.add(container.title)
                return getPath(container.parentUid, path)
            }
        }
        return path
    }

    fun getContainer(parentId: String): Flow<Container> {
        return memoDao.container(parentId).map { it.toDomain() }
    }

    fun getLocation(memoId: String): Flow<Location?> {
        return memoDao.location(memoId).mapNotNull { it?.toDomain() }
    }

    suspend fun getMemo(id: String?): Memo? {
        if (id == null) {
            return null
        }
        val memo = memoDao.getMemo(id).toDomain()
        memo.hasLocation = memoDao.countLocations(memo.uid) > 0
        return memo
    }

    fun elements(containerId: String?): Flow<List<Any>> {
        val memosSource = when (containerId) {
            null -> memoDao.memos()
            else -> memoDao.memos(containerId)
        }
            .map { memoDtos ->
                memoDtos.map {
                    val memo = it.toDomain()
                    memo.hasLocation = memoDao.countLocations(memo.uid) > 0
                    memo.categoryName = memoDao.getCategory(memo.categoryUid).name
                    memo
                }
            }
        return when (containerId) {
            null -> memoDao.containers()
            else -> memoDao.containers(containerId)
        }.map { containerDtos ->
            containerDtos.map {
                val container = it.toDomain()
                container.childrenCount = countChildren(it.uid)
                container
            }
        }.combine(memosSource) { containers, memos ->
            containers + memos
        }
    }

    private suspend fun countChildren(containerUid: String): Int {
        val childContainersCount = memoDao.countContainers(containerUid)
        val memosCount = memoDao.countMemos(containerUid)
        return childContainersCount + memosCount
    }

    suspend fun add(memo: Memo) {
        withContext(Dispatchers.IO) {
            memoDao.add(memo.mapToDto())
        }
    }

    suspend fun update(memo: Memo) {
        withContext(Dispatchers.IO) {
            memoDao.update(memo.mapToDto())
        }
    }

    suspend fun update(memoId: String, lat: Double, lng: Double) {
        withContext(Dispatchers.IO) {
            memoDao.updateLocation(memoId, lat, lng)
        }
    }

    suspend fun add(container: Container) {
        withContext(Dispatchers.IO) {
            memoDao.add(container.mapToDto())
        }
    }

    suspend fun add(location: Location) {
        withContext(Dispatchers.IO) {
            memoDao.add(location.mapToDto())
        }
    }

    suspend fun delete(container: Container) {
        memoDao.deleteContainer(container.uid)
    }

    suspend fun delete(memo: Memo) {
        memoDao.deleteMemo(memo.uid)
    }

    private fun Memo.mapToDto(): MemoDto {
        return MemoDto(
            this.uid,
            parentUid,
            title,
            timestamp,
            categoryUid,
            subtitle,
            description,
            link,
            dateTime,
            flag,
            reminder
        )
    }

    private fun Container.mapToDto(): ContainerDto {
        return ContainerDto(
            this.uid,
            parentUid,
            title,
            subtitle,
            timestamp
        )
    }

    private fun Location.mapToDto(): LocationDto {
        return LocationDto(
            this.uid,
            memoId,
            latitude,
            longitude
        )
    }
}
