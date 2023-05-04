package com.darekbx.dotpad.di

import android.content.Context
import com.darekbx.dotpad.BuildConfig
import com.darekbx.dotpad.reminder.ReminderCreator
import com.darekbx.dotpad.viewmodel.DotsViewModel
import com.darekbx.storage.dotpad.DotsDao
import com.darekbx.storage.legacy.DotPadHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideReminderCreator(@ApplicationContext context: Context): ReminderCreator {
        return ReminderCreator(context.contentResolver, BuildConfig.USER_EMAIL)
    }

    @Provides
    fun provideDotsViewModel(
        dotsDao: DotsDao,
        reminderCreator: ReminderCreator,
        dotPadHelper: DotPadHelper?
    ): DotsViewModel {
        return DotsViewModel(dotsDao, reminderCreator, dotPadHelper)
    }
}
