package com.darekbx.spreadsheet.di

import android.content.ContentResolver
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.darekbx.spreadsheet.domain.CellUseCases
import com.darekbx.spreadsheet.domain.ImportUseCase
import com.darekbx.spreadsheet.domain.SpreadSheetUseCases
import com.darekbx.spreadsheet.domain.SynchronizeUseCase
import com.darekbx.spreadsheet.repository.SettingsRepository
import com.darekbx.spreadsheet.synchronize.CompressionUtils
import com.darekbx.spreadsheet.synchronize.FirebaseHelper
import com.darekbx.spreadsheet.ui.grid.bus.SpreadSheetBus
import com.darekbx.spreadsheet.ui.grid.viewmodel.CellsLoader
import com.darekbx.storage.spreadsheet.CellDao
import com.darekbx.storage.spreadsheet.SpreadSheetDao
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideCompressionUtils(): CompressionUtils {
        return CompressionUtils()
    }

    @Provides
    fun provideFirebaseHelperprovideFirebaseHelper(@ApplicationContext context: Context): FirebaseHelper {
        return FirebaseHelper(context)
    }

    @Provides
    fun provideSynchronizeUseCase(
        auth: FirebaseHelper,
        settings: SettingsRepository,
        spreadSheetUseCases: SpreadSheetUseCases,
        cellUseCases: CellUseCases,
        gson: Gson
    ): SynchronizeUseCase {
        return SynchronizeUseCase(
            auth,
            settings,
            spreadSheetUseCases,
            cellUseCases,
            gson
        )
    }

    @Singleton
    @Provides
    fun provideSpreadSheetBus(): SpreadSheetBus {
        return SpreadSheetBus()
    }

    @Provides
    fun provideSettingsRepository(dataStore: DataStore<Preferences>): SettingsRepository {
        return SettingsRepository(dataStore)
    }

    @Singleton
    @Provides
    fun provideCellsLoader(cellUseCases: CellUseCases): CellsLoader {
        return CellsLoader(cellUseCases)
    }

    @Provides
    fun provideSpreadSheetUseCases(
        spreadSheetDao: SpreadSheetDao,
        cellDao: CellDao,
        settings: SettingsRepository
    ): SpreadSheetUseCases {
        return SpreadSheetUseCases(spreadSheetDao, cellDao, settings)
    }

    @Provides
    fun provideCellUseCases(
        cellDao: CellDao,
        spreadSheetDao: SpreadSheetDao,
        settings: SettingsRepository
    ): CellUseCases {
        return CellUseCases(cellDao, spreadSheetDao, settings)
    }

    @Provides
    fun provideImportUseCase(
        spreadSheetDao: SpreadSheetDao,
        cellDao: CellDao,
        contentResolver: ContentResolver
    ): ImportUseCase {
        return ImportUseCase(spreadSheetDao, cellDao, contentResolver)
    }
}