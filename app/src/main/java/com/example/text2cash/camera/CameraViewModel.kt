package com.example.text2cash.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.UserPreferences
import com.example.domain.recognition.OcrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CameraViewModel @Inject constructor(
    private val ocrRepository: OcrRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {
    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Idle)
    val uiState: StateFlow<CameraUiState> = _uiState

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    fun takePhotoAndRecognize() {
        // processImage(uri)
        // TODO: добавить CameraX ImageCapture
        // когда получаем Uri фотографии -> processImage(uri)
    }

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
        processImage(uri)
    }

    fun processImage(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = CameraUiState.Loading
            try {
                val text = ocrRepository.recognizeText(uri)
                _uiState.value = CameraUiState.Success(text)
            } catch (e: Exception) {
                _uiState.value = CameraUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun recognizeText(uri: Uri) {
        viewModelScope.launch {
            try {
                var rawText = ocrRepository.recognizeText(uri)

                val language = userPreferences.selectedLanguage
                val currency = userPreferences.selectedCurrency

                // println("qwqwqwqwqw rawText = $rawText")

                // TODO: implement translated
            }
            // TODO: Handle more specific exceptions
            catch (e: Exception) {
                _uiState.value = CameraUiState.Error(message = e.message ?: "Unknown error")
            }
        }
    }
}
