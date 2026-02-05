package com.darekbx.spreadsheet.domain

import com.darekbx.spreadsheet.repository.SettingsRepository
import com.darekbx.spreadsheet.synchronize.FirebaseHelper
import com.darekbx.storage.spreadsheet.entities.CellDto
import com.darekbx.storage.spreadsheet.entities.SpreadSheetDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2

sealed class SyncStatus {
    data object Idle : SyncStatus()
    data class UpToDate(val localVersion: Long, val remoteVersion: Long) : SyncStatus()
    data class Success(val maxBlobSize: Long) : SyncStatus()
    data class InProgress(val progress: Float) : SyncStatus()
    data class Error(val e: Throwable) : SyncStatus()
}

class SynchronizeUseCase @Inject constructor(
    private val firebaseHelper: FirebaseHelper,
    private val settingsRepository: SettingsRepository,
    private val spreadSheetUseCases: SpreadSheetUseCases,
    private val cellUseCases: CellUseCases,
    private val gson: Gson
) {

    private var remoteVersion: Long = EMPTY_VERSION
    private var localVersion: Long = EMPTY_VERSION

    suspend fun shouldSynchronize(): Boolean {
        // 1.Authorize
        firebaseHelper.checkAndAuthorize()

        // 2. Get remote version
        remoteVersion = getRemoteVersion()

        // 3. Get local version
        localVersion = settingsRepository.getLocalVersion()

        // 4. if any version is unset return true
        if (remoteVersion == UNSET_REMOTE_VERSION || localVersion == SettingsRepository.UNSET_LOCAL_VERSION) {
            return true
        }

        // 5. if versions are different return true, otherwise false
        val versionsNotEqual = remoteVersion != localVersion

        return versionsNotEqual
    }

    fun synchronize() = flow {
        try {
            emit(SyncStatus.Idle)
            when {
                remoteVersion == UNSET_REMOTE_VERSION -> localToCloud()
                localVersion < remoteVersion -> cloudToLocal()
                localVersion > remoteVersion -> localToCloud()
                else -> emit(SyncStatus.UpToDate(localVersion, remoteVersion))
            }
        } catch (e: Exception) {
            emit(SyncStatus.Error(e))
        }
    }

    private suspend fun FlowCollector<SyncStatus>.localToCloud() {
        var stepsCount = 0F
        try {
            // 1. Collect local data
            val spreadSheets = spreadSheetUseCases.fetchDataForSync()
                .also {
                    stepsCount = it.size + 3F
                    emit(SyncStatus.InProgress(1F / stepsCount))
                }

            // 2. Convert to firebase format
            val convertedLocalData = convertLocalToRemote(spreadSheets)
                .also { emit(SyncStatus.InProgress(2F / stepsCount))  }

            // 3. Check max blob size
            val maxBlobSize = convertedLocalData.values.maxOf { it.length }.toLong()

            // 4. Remove cloud data
            deleteCloudData()
                .also { emit(SyncStatus.InProgress(3F / stepsCount))  }

            // 5. Upload local data
            uploadSpreadSheet(convertedLocalData) { current, _ ->
                emit(SyncStatus.InProgress((current + 3F) / stepsCount))
            }

            // 6. Update remote version
            updateRemoteVersion()
                .also { emit(SyncStatus.InProgress(1F))  }

            emit(SyncStatus.Success(maxBlobSize))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(SyncStatus.Error(e))
        }
    }

    private suspend fun FlowCollector<SyncStatus>.cloudToLocal() {
        /*
             1. Download all remote data
             2. Remove local data
             3. Save data from remote to local
             x. If failed, remote all local data, don't update vesrion.
             3. Update cloud version
        */

    }

    private fun convertLocalToRemote(data: Map<SpreadSheetDto, List<CellDto>>): Map<SpreadSheetDto, String> {
        return data.mapValues { entry -> gson.toJson(entry.value) }
    }

    private suspend fun updateRemoteVersion() {
        val version = if (localVersion == SettingsRepository.UNSET_LOCAL_VERSION) 1 else localVersion
        setRemoteVersion(version)
    }

    private suspend fun uploadSpreadSheet(
        data: Map<SpreadSheetDto, String>,
        progress: suspend (Int, Int) -> Unit
    ) {
        val collection = provideFirestore()
            .collection(CLOUD_SPREADSHETS_TABLE)

        var index = 0
        for ((spreadSheet, cellsBlob) in data) {
            collection
                .add(
                    hashMapOf(
                        "uid" to spreadSheet.uid,
                        "name" to spreadSheet.name,
                        "parent_name" to spreadSheet.parentName,
                        "parent_uid" to spreadSheet.parentUid,
                        "created_timestamp" to spreadSheet.createdTimestamp,
                        "updated_timestamp" to spreadSheet.updatedTimestamp,
                        "cells" to cellsBlob
                    )
                )
                .await()
            progress(index++, data.size)
        }
    }

    private suspend fun deleteCloudData() {
        val documents = provideFirestore()
            .collection(CLOUD_SPREADSHETS_TABLE)
            .get()
            .await()
        documents.forEach { document ->
            document.reference
                .delete()
                .await()
        }
    }

    private suspend fun getRemoteVersion(): Long {
        val documents = provideFirestore()
            .collection(CLOUD_VERSION_TABLE)
            .get()
            .await()
        val remoteVersion = documents.firstOrNull()?.getLong(CLOUD_VERSION_KEY)
        return remoteVersion ?: UNSET_REMOTE_VERSION
    }

    private suspend fun setRemoteVersion(version: Long) {
        provideFirestore()
            .collection(CLOUD_VERSION_TABLE)
            .document(CLOUD_VERSION_DOC)
            .set(hashMapOf(CLOUD_VERSION_KEY to version))
            .await()
    }

    private fun provideFirestore(): FirebaseFirestore = Firebase
        .firestore(firebaseHelper.spreadsheetApp)

    companion object {
        private const val CLOUD_VERSION_TABLE = "version"
        private const val CLOUD_VERSION_DOC = "current"
        private const val CLOUD_VERSION_KEY = "value"
        private const val UNSET_REMOTE_VERSION = -1L
        private const val EMPTY_VERSION = -2L
        private const val CLOUD_SPREADSHETS_TABLE = "spreadsheets"
    }
}