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
    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState

    fun recognizeText(uri: Uri) {
        viewModelScope.launch {
            try {
                var rawText = ocrRepository.recognizeText(uri)

                val language = userPreferences.selectedLanguage
                val currency = userPreferences.selectedCurrency

                println("qwqwqwqwqw rawText = $rawText")

                // TODO: implement translated
            }
            // TODO: Handle more specific exceptions
            catch (e: Exception) {
                _uiState.value = CameraUiState(errorMessage = e.message ?: "Unknown error")
            }
        }
    }
}

data class CameraUiState(
    val isLoading: Boolean = false,
    val recognizedText: String? = null,
    val errorMessage: String? = null,
    val imageUri: Uri? = null
)