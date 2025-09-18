package com.example.text2cash.camera

import android.net.Uri

data class CameraUiState(
    val isLoading: Boolean = false,
    val recognizedText: String? = null,
    val errorMessage: String? = null,
    val imageUri: Uri? = null
)