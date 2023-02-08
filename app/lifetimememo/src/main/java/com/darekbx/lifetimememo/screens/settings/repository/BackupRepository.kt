package com.darekbx.lifetimememo.screens.settings.repository

import com.darekbx.storage.lifetimememo.BackupDao
import com.darekbx.storage.lifetimememo.CategoryDto
import com.darekbx.lifetimememo.data.dto.ContainerDto
import com.darekbx.storage.lifetimememo.LocationDto
import com.darekbx.storage.lifetimememo.MemoDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BackupRepository @Inject constructor(
    private val backupDao: BackupDao
) {

    fun allMemos(): Flow<List<MemoDto>> = backupDao.allMemos()

    fun allContainers(): Flow<List<ContainerDto>> = backupDao.allContainers()

    fun allCategories(): Flow<List<CategoryDto>> = backupDao.allCategories()

    fun allLocations(): Flow<List<LocationDto>> = backupDao.allLocations()

    suspend fun addMemos(data: List<MemoDto>) {
        backupDao.addMemos(data)
    }

    suspend fun addCategories(data: List<CategoryDto>) {
        backupDao.addCategories(data)
    }

    suspend fun addContainers(data: List<ContainerDto>) {
        backupDao.addContainers(data)
    }

    suspend fun addLocations(data: List<LocationDto>) {
        backupDao.addLocations(data)
    }
}