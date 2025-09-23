package com.example.text2cash.camera

import android.net.Uri

sealed class CameraUiState {
    object Idle : CameraUiState()
    object Loading : CameraUiState()
    data class Success(val text: String) : CameraUiState()
    data class Error(val message: String) : CameraUiState()
}