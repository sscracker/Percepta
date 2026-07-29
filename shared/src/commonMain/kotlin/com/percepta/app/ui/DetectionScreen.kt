package com.percepta.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.percepta.app.rememberCameraCaptureAction
import com.percepta.app.rememberImagePickerAction

@Composable
fun DetectionScreen(
    viewModel: DetectionViewModel = viewModel { DetectionViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pickImage = rememberImagePickerAction { image ->
        viewModel.onImageSelected(image)
    }
    val captureImage = rememberCameraCaptureAction {
        viewModel.onImageSelected(it)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is DetectionUiState.Idle -> Text("Pick an image to detect objects")
                is DetectionUiState.Loading -> {
                    Image(
                        bitmap = state.imageBitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                    CircularProgressIndicator()
                }
                is DetectionUiState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            bitmap = state.imageBitmap,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        )
                        Text("Found ${state.objects.size} objects")
                    }
                }
                is DetectionUiState.Error ->
                    Text("Error: ${state.message}")
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { captureImage() },
            modifier = Modifier.fillMaxWidth()
            ) {
                Text("Camera")
            }
            Button(
                onClick = { pickImage() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gallery")
            }
    }
}