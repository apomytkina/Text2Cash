package com.example.domain.recognition

import android.net.Uri
import com.example.data.recognition.OCRDataSource
import javax.inject.Inject
import javax.inject.Singleton

interface OcrRepository {
    suspend fun recognizeText(uri: Uri): String
}

@Singleton
class OcrRepositoryImpl @Inject constructor(
    private val ocrDataSource: OCRDataSource,
) : OcrRepository {
    override suspend fun recognizeText(uri: Uri): String = ocrDataSource.recognizeText(uri)
}