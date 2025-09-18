package com.darekbx.notebookcheckreader.di

import android.app.NotificationManager
import android.content.Context
import com.darekbx.notebookcheckreader.domain.AddRemoveToFavouritesUseCase
import com.darekbx.notebookcheckreader.domain.DeleteOldItemsUseCase
import com.darekbx.notebookcheckreader.domain.FetchFavouriteItemsUseCase
import com.darekbx.notebookcheckreader.domain.FetchFavouritesCountUseCase
import com.darekbx.notebookcheckreader.domain.FetchItemsCountUseCase
import com.darekbx.notebookcheckreader.domain.FetchRssItemsUseCase
import com.darekbx.notebookcheckreader.domain.MarkReadItemsUseCase
import com.darekbx.notebookcheckreader.domain.SynchronizeUseCase
import com.darekbx.notebookcheckreader.repository.Filters
import com.darekbx.notebookcheckreader.repository.RssNotificationManager
import com.darekbx.notebookcheckreader.repository.remote.RssFetch
import com.darekbx.notebookcheckreader.repository.remote.RssParser
import com.darekbx.storage.notebookcheckreader.RssDao
import com.darekbx.storage.notebookcheckreader.RssFavouritesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Named("feed_url")
    fun provideFeedUrl(): String = "https://www.notebookcheck.net/News.152.100.html"

    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.BODY
            }
        }
    }

    @Provides
    @Singleton
    fun provideRssParser(): RssParser = RssParser()

    @Provides
    fun provideRssFetch(httpClient: HttpClient, rssParser: RssParser): RssFetch {
        return RssFetch(httpClient, rssParser)
    }

    @Provides
    fun provideRssNotificationManager(
        @ApplicationContext context: Context,
        notificationManager: NotificationManager
    ): RssNotificationManager {
        return RssNotificationManager(context, notificationManager)
    }

    @Provides
    @Singleton
    fun provideFilters(): Filters = Filters()
}

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideSynchronizeUseCase(
        rssFetch: RssFetch,
        rssDao: RssDao,
        @Named("feed_url") feedUrl: String,
        filters: Filters
    ): SynchronizeUseCase {
        return SynchronizeUseCase(rssFetch, rssDao, feedUrl, filters)
    }

    @Provides
    @Singleton
    fun provideFetchRssItemsUseCase(
        rssDao: RssDao,
        favouritesDao: RssFavouritesDao
    ): FetchRssItemsUseCase {
        return FetchRssItemsUseCase(rssDao, favouritesDao)
    }

    @Provides
    @Singleton
    fun provideFetchFavouriteItemsUseCase(
        rssDao: RssDao,
        favouritesDao: RssFavouritesDao
    ): FetchFavouriteItemsUseCase {
        return FetchFavouriteItemsUseCase(rssDao, favouritesDao)
    }

    @Provides
    @Singleton
    fun provideMarkReadItemsUseCase(rssDao: RssDao): MarkReadItemsUseCase {
        return MarkReadItemsUseCase(rssDao)
    }

    @Provides
    @Singleton
    fun provideAddRemoveToFavouritesUseCase(favouritesDao: RssFavouritesDao): AddRemoveToFavouritesUseCase {
        return AddRemoveToFavouritesUseCase(favouritesDao)
    }

    @Provides
    @Singleton
    fun provideFetchFavouritesCountUseCase(favouritesDao: RssFavouritesDao): FetchFavouritesCountUseCase {
        return FetchFavouritesCountUseCase(favouritesDao)
    }

    @Provides
    @Singleton
    fun provideFetchItemsCountUseCase(rssDao: RssDao): FetchItemsCountUseCase {
        return FetchItemsCountUseCase(rssDao)
    }

    @Provides
    @Singleton
    fun provideDeleteOldItemsUseCase(
        rssDao: RssDao,
        favouritesDao: RssFavouritesDao
    ): DeleteOldItemsUseCase {
        return DeleteOldItemsUseCase(rssDao, favouritesDao)
    }
}
