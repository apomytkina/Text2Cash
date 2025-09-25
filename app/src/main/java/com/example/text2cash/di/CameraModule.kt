package com.example.text2cash.di

import android.content.Context
import com.example.domain.recognition.CameraRepository
import com.example.domain.recognition.CameraRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CameraModule {
    @Provides
    @Singleton
    fun providesCameraRepository(
        @ApplicationContext context: Context,
    ): CameraRepository {
        return CameraRepositoryImpl(context)
    }
}