package com.example.text2cash.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@Composable
internal fun CameraScreen(
    navController: NavController,
    viewModel: CameraViewModel = hiltViewModel(),
){
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val lifecycleOwner = LocalContext.current as LifecycleOwner

    val galleryPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
    }

    LaunchedEffect(previewView) {
        viewModel.startCamera(lifecycleOwner, previewView)
    }

    val galleryPickerLauncher = rememberGalleryPickerLauncher(viewModel)
    val cameraPermissionLauncher = rememberCameraPermissionLauncher(context, viewModel)
    val galleryPermissionLauncher = rememberGalleryPermissionLauncher(
        context,
        galleryPickerLauncher
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ImageOrCameraSurface(selectedImageUri, previewView)

        CameraControls(
            modifier = Modifier.align(Alignment.BottomCenter),
            context = context,
            galleryPermission = galleryPermission,
            galleryPickerLauncher = galleryPickerLauncher,
            galleryPermissionLauncher = galleryPermissionLauncher,
            cameraPermissionLauncher = cameraPermissionLauncher,
            uiState = uiState,
            viewModel = viewModel,
        )
    }
}

@Composable
private fun ImageOrCameraSurface(
    selectedImageUri: Uri?,
    previewView: PreviewView,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        selectedImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        } ?: run {
            AndroidView(
                factory = { ctx -> previewView },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun CameraControls(
    modifier: Modifier = Modifier,
    context: Context,
    galleryPermission: String,
    galleryPickerLauncher: ActivityResultLauncher<String>,
    cameraPermissionLauncher: ActivityResultLauncher<String>,
    galleryPermissionLauncher: ActivityResultLauncher<String>,
    uiState: CameraUiState,
    viewModel: CameraViewModel
) {
    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                enabled = uiState !is CameraUiState.Loading,
                onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED) {
                        viewModel.takePhotoAndRecognize()
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
            ) {
                Text("Photo")
            }

            Button(
                enabled = uiState !is CameraUiState.Loading,
                onClick = {
                    if (
                        Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU ||
                        ContextCompat.checkSelfPermission(context, galleryPermission) == PackageManager.PERMISSION_GRANTED
                    ) {
                        galleryPickerLauncher.launch("image/*")
                    } else {
                        galleryPermissionLauncher.launch(galleryPermission)
                    }
                },
            ) {
                Text("Gallery")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is CameraUiState.Idle -> println("Сделайте фото или выберите картинку")
            is CameraUiState.Loading -> println("Сканирую...")
            is CameraUiState.Success -> println("Распознанный текст: ${uiState.text}")
            is CameraUiState.Error -> println("Ошибка: ${uiState.message}")
        }
    }
}

@Composable
private fun rememberGalleryPickerLauncher(
    viewModel: CameraViewModel,
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { uri: Uri? ->
    uri?.let {
        viewModel.onImageSelected(it)
    }
}

@Composable
private fun rememberCameraPermissionLauncher(
    context: Context,
    viewModel: CameraViewModel,
) = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
) { granted ->
    if (granted) {
        viewModel.takePhotoAndRecognize()
    } else {
        Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun rememberGalleryPermissionLauncher(
    context: Context,
    galleryPickerLauncher: ActivityResultLauncher<String>
) = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
) { granted ->
    if (granted) {
        galleryPickerLauncher.launch("image/*")
    } else {
        Toast.makeText(context, "Gallery permission denied", Toast.LENGTH_SHORT).show()
    }
}