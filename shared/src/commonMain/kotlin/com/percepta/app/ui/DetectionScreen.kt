package com.percepta.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val textMeasurer = rememberTextMeasurer()

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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                bitmap = state.imageBitmap,
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                            Canvas(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                val imgWidth = state.imageBitmap.width.toFloat()
                                val imgHeight = state.imageBitmap.height.toFloat()
                                val scale = minOf(size.width / imgWidth, size.height / imgHeight)
                                val drawnWidth = imgWidth * scale
                                val drawnHeight = imgHeight * scale
                                val offsetX = (size.width - drawnWidth) / 2f
                                val offsetY = (size.height - drawnHeight) / 2f

                                state.objects.forEachIndexed { index, obj ->
                                    val b = obj.boundingBox
                                    val left = offsetX + b.left * drawnWidth
                                    val top = offsetY + b.top * drawnHeight
                                    drawRect(
                                        color = Color.Red,
                                        topLeft = Offset(left, top),
                                        size = Size(b.width * drawnWidth, b.height * drawnHeight),
                                        style = Stroke(width = 3.dp.toPx())
                                    )
                                    drawText(
                                        textMeasurer = textMeasurer,
                                        text = "${index + 1}",
                                        topLeft = Offset(left, top),
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            background = Color.Red
                                        )
                                    )
                                }
                            }
                        }
                        if (state.objects.isEmpty()){
                            Text("No objects found")
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 150.dp)
                                    .verticalScroll(rememberScrollState()),
                            ) {
                                state.objects.forEachIndexed { index, obj ->
                                    Text("${index + 1}. ${obj.label} - ${(obj.confidence * 100).toInt()}%")
                                }
                            }
                        }
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