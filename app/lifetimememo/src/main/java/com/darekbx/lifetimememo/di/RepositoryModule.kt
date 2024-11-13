package com.darekbx.lifetimememo.di

import android.content.ClipboardManager
import android.content.Context
import com.darekbx.storage.lifetimememo.MemoDao
import com.darekbx.lifetimememo.screens.category.repository.CategoryRepository
import com.darekbx.lifetimememo.screens.memos.repository.BaseMemosRepository
import com.darekbx.lifetimememo.screens.memos.repository.MemosRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideClipboardManager(@ApplicationContext context: Context): ClipboardManager {
        return context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    @Provides
    fun provideCategoryRepository(memoDao: MemoDao): CategoryRepository {
        return CategoryRepository(memoDao)
    }

  /*  @Provides
    fun provideGson(): Gson {
        return Gson()
    }*/

    @Provides
    fun provideBaseMemosRepository(memoDao: MemoDao): BaseMemosRepository {
        return MemosRepository(memoDao)
    }
}