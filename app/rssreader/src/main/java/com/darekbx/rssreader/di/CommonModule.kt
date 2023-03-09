package com.darekbx.rssreader.di

import android.content.Context
import com.prof.rssparser.Parser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideParser(@ApplicationContext context: Context) : Parser {
        return Parser.Builder()
            .context(context)
            .cacheExpirationMillis(24L * 60L * 60L * 1000L) // one day
            .build()
    }
}
