package com.example.text2cash.camera

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
internal fun CameraScreen(
    navController: NavController,
    viewModel: CameraViewModel = hiltViewModel(),
){
    val context = LocalContext.current
    val recognizerState by viewModel.uiState.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.recognizeText(uri) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Camera & OCR", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { galleryLauncher.launch("image/*") }
        ) {
            Text("Pick from Gallery")
        }

        Spacer(Modifier.height(16.dp))
        Text("Recognized:", style = MaterialTheme.typography.bodyMedium)
        Text(recognizerState.recognizedText.orEmpty(), modifier = Modifier.padding(8.dp))
    }
}