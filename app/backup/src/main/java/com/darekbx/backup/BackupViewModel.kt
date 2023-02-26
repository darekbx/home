package com.darekbx.backup

import android.content.Context
import android.os.ParcelFileDescriptor
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val dataStore: DataStore<Preferences>
) :
    ViewModel() {

    fun makeBackup(pfd: ParcelFileDescriptor) {
        viewModelScope.launch {
            FileOutputStream(pfd.fileDescriptor).use { outputStream ->
                val localDatabaseFile = context.getDatabasePath(context.databaseList()[0])
                localDatabaseFile.inputStream().use { input ->
                    input.copyTo(outputStream)
                }
                updateLastBackupDate()
            }
        }
    }

    val lastBackupDate = dataStore.data.map { preferences ->
        val value = preferences[LAST_BACKUP_DATE]
        if (value != null) {
            BACKUP_DATE_FORMAT.format(value)
        } else {
            ""
        }
    }

    private suspend fun updateLastBackupDate() {
        dataStore.edit { preferences ->
            preferences[LAST_BACKUP_DATE] = System.currentTimeMillis()
        }
    }

    companion object {
        private val BACKUP_DATE_FORMAT =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        private val LAST_BACKUP_DATE = longPreferencesKey("last_backup_date")
    }
}