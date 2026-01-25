package com.darekbx.emailbot.di

import android.app.NotificationManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.darekbx.emailbot.BuildConfig
import com.darekbx.emailbot.bot.CleanUpBot
import com.darekbx.emailbot.domain.AddSpamFilterUseCase
import com.darekbx.emailbot.domain.DeleteSpamFilterUseCase
import com.darekbx.emailbot.domain.FetchSpamFiltersUseCase
import com.darekbx.emailbot.imap.Connection
import com.darekbx.emailbot.imap.EmailOperations
import com.darekbx.emailbot.imap.FetchEmails
import com.darekbx.emailbot.repository.RefreshBus
import com.darekbx.emailbot.repository.storage.CommonPreferences
import com.darekbx.emailbot.repository.storage.CryptoUtils
import com.darekbx.emailbot.repository.storage.EncryptedConfiguration
import com.darekbx.emailbot.worker.BotNotificationManager
import com.darekbx.storage.emailbot.SpamDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmailBotModule {

    @Provides
    @Singleton
    @Named("master.key")
    fun provideMasterKey(): String {
        return BuildConfig.MASTER_KEY
    }

    @Provides
    @Singleton
    fun provideCryptoUtils(@Named("master.key") masterKey: String): CryptoUtils {
        return CryptoUtils(masterKey)
    }

    @Provides
    @Singleton
    fun provideEncryptedConfiguration(
        dataStore: DataStore<Preferences>,
        cryptoUtils: CryptoUtils
    ): EncryptedConfiguration {
        return EncryptedConfiguration(dataStore, cryptoUtils)
    }

    @Provides
    @Singleton
    fun provideCommonPreferences(
        @ApplicationContext context: Context
    ): CommonPreferences {
        return CommonPreferences(context)
    }

    @Provides
    fun provideCleanUpBot(
        fetchEmails: FetchEmails,
        emailOperations: EmailOperations,
        spamDao: SpamDao,
        refreshBus: RefreshBus,
        commonPreferences: CommonPreferences
    ): CleanUpBot {
        return CleanUpBot(fetchEmails, emailOperations, spamDao, refreshBus, commonPreferences)
    }

    @Provides
    @Singleton
    fun provideRefreshBus(): RefreshBus {
        return RefreshBus()
    }

    @Provides
    fun provideBotNotificationManager(
        @ApplicationContext context: Context,
        notificationManager: NotificationManager
    ): BotNotificationManager {
        return BotNotificationManager(context, notificationManager)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    fun provideAddSpamFilterUseCase(spamDao: SpamDao): AddSpamFilterUseCase {
        return AddSpamFilterUseCase(spamDao)
    }

    @Provides
    fun provideFetchSpamFiltersUseCase(spamDao: SpamDao): FetchSpamFiltersUseCase {
        return FetchSpamFiltersUseCase(spamDao)
    }

    @Provides
    fun provideDeleteSpamFilterUseCase(spamDao: SpamDao): DeleteSpamFilterUseCase {
        return DeleteSpamFilterUseCase(spamDao)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ImapModule {

    @Provides
    fun provideConnection(): Connection {
        return Connection()
    }

    @Provides
    fun provideFetchEmails(
        connection: Connection,
        encryptedConfiguration: EncryptedConfiguration
    ): FetchEmails {
        return FetchEmails(connection, encryptedConfiguration)
    }

    @Provides
    fun provideEmailOperations(
        connection: Connection,
        encryptedConfiguration: EncryptedConfiguration
    ): EmailOperations {
        return EmailOperations(connection, encryptedConfiguration)
    }
}
