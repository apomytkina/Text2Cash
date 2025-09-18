package com.example.text2cash.di

import android.content.Context
import com.example.data.recognition.OCRDataSource
import com.example.domain.recognition.OcrRepository
import com.example.domain.recognition.OcrRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class OCRModule {

    @Provides
    @Singleton
    fun provideDataSource(
        @ApplicationContext context: Context,
    ): OCRDataSource {
        return OCRDataSource(context)
    }

    @Provides
    @Singleton
    fun provideOcrRepository(ocrDataSource: OCRDataSource): OcrRepository {
        return OcrRepositoryImpl(ocrDataSource)
    }
}