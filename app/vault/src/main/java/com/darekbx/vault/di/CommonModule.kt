package com.darekbx.vault.di

import com.darekbx.vault.security.Encryption
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideEncryption() = Encryption()
}