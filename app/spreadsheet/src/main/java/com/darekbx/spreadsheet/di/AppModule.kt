package com.darekbx.spreadsheet.di

import android.content.ContentResolver
import android.content.Context
import com.darekbx.spreadsheet.domain.CellUseCases
import com.darekbx.spreadsheet.domain.ImportUseCase
import com.darekbx.spreadsheet.domain.SpreadSheetUseCases
import com.darekbx.spreadsheet.ui.grid.bus.SpreadSheetBus
import com.darekbx.spreadsheet.ui.grid.viewmodel.CellsLoader
import com.darekbx.storage.spreadsheet.CellDao
import com.darekbx.storage.spreadsheet.SpreadSheetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideSpreadSheetBus(): SpreadSheetBus {
        return SpreadSheetBus()
    }

    @Singleton
    @Provides
    fun provideCellsLoader(cellUseCases: CellUseCases): CellsLoader {
        return CellsLoader(cellUseCases)
    }

    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    fun provideSpreadSheetUseCases(
        spreadSheetDao: SpreadSheetDao,
        cellDao: CellDao
    ): SpreadSheetUseCases {
        return SpreadSheetUseCases(spreadSheetDao, cellDao)
    }

    @Provides
    fun provideCellUseCases(
        cellDao: CellDao,
        spreadSheetDao: SpreadSheetDao
    ): CellUseCases {
        return CellUseCases(cellDao, spreadSheetDao)
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