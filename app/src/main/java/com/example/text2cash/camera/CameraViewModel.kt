package com.example.text2cash.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.UserPreferences
import com.example.domain.recognition.CameraRepository
import com.example.domain.recognition.OcrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ocrRepository: OcrRepository,
    private val cameraRepository: CameraRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {
    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Idle)
    val uiState: StateFlow<CameraUiState> = _uiState

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private var imageCapture: ImageCapture? = null

    fun setImageCapture(capture: ImageCapture) {
        imageCapture = capture
    }

    fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
    ) {
        cameraRepository.startCamera(
            lifecycleOwner = lifecycleOwner,
            previewView = previewView,
            onImageCaptureReady = { imageCapture ->
                setImageCapture(imageCapture)
            }
        )
    }

    fun takePhotoAndRecognize() {
        val imageCapture = imageCapture ?: return

        val photoFile = cameraRepository.createPhotoFile()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val uri = cameraRepository.getUriForFile(file = photoFile)
                    _selectedImageUri.value = uri
                    processImage(uri)
                }

                override fun onError(exception: ImageCaptureException) {
                    _uiState.value = CameraUiState.Error("Capture failed: ${exception.message}")
                }
            }
        )
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
//            finally {
//                _selectedImageUri.value = null
//            }
        }
    }
}
