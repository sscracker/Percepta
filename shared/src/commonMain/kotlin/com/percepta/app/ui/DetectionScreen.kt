package com.percepta.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.CenterFocusWeak
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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
import com.percepta.app.detection.DetectedObject
import com.percepta.app.rememberCameraCaptureAction
import com.percepta.app.rememberImagePickerAction
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionScreen(
    viewModel: DetectionViewModel = viewModel { DetectionViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pickImage = rememberImagePickerAction { viewModel.onImageSelected(it) }
    val captureImage = rememberCameraCaptureAction { viewModel.onImageSelected(it) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Percepta", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                when (val state = uiState) {
                    is DetectionUiState.Idle -> EmptyState()
                    is DetectionUiState.Loading ->
                        ImageWithOverlay(state.imageBitmap, emptyList(), loading = true)
                    is DetectionUiState.Success -> Column(Modifier.fillMaxSize()) {
                        Box(Modifier.fillMaxWidth().weight(1f)) {
                            ImageWithOverlay(state.imageBitmap, state.objects, loading = false)
                        }
                        Spacer(Modifier.size(12.dp))
                        ResultsList(state.objects)
                    }
                    is DetectionUiState.Error -> ErrorState(state.message)
                }
            }

            Spacer(Modifier.size(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FilledTonalButton(onClick = { pickImage() }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.PhotoLibrary, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("Gallery")
                }
                FilledTonalButton(onClick = { captureImage() }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.PhotoCamera, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("Camera")
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Outlined.CenterFocusWeak,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.size(16.dp))
        Text(
            "Detect objects in a photo",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.size(4.dp))
        Text(
            "Pick a photo from your gallery or take one with the camera.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ErrorState(message: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Outlined.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.error,
        )
        Spacer(Modifier.size(12.dp))
        Text(
            "Detection failed",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.size(4.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ImageWithOverlay(
    bitmap: ImageBitmap,
    objects: List<DetectedObject>,
    loading: Boolean,
) {
    val textMeasurer = rememberTextMeasurer()
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
            )
            Canvas(Modifier.fillMaxSize()) {
                val iw = bitmap.width.toFloat()
                val ih = bitmap.height.toFloat()
                val scale = minOf(size.width / iw, size.height / ih)
                val dw = iw * scale
                val dh = ih * scale
                val ox = (size.width - dw) / 2f
                val oy = (size.height - dh) / 2f
                objects.forEachIndexed { index, obj ->
                    val b = obj.boundingBox
                    val color = colorForClass(obj.classId)
                    val left = ox + b.left * dw
                    val top = oy + b.top * dh
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(left, top),
                        size = Size(b.width * dw, b.height * dh),
                        cornerRadius = CornerRadius(10f, 10f),
                        style = Stroke(width = 3.dp.toPx()),
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "${index + 1}",
                        topLeft = Offset(left + 4f, top + 4f),
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            background = color,
                        ),
                    )
                }
            }
            if (loading) {
                Surface(color = Color.Black.copy(alpha = 0.35f), modifier = Modifier.fillMaxSize()) {}
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(Modifier.size(8.dp))
                    Text("Analyzing…", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun ResultsList(objects: List<DetectedObject>) {
    if (objects.isEmpty()) {
        Text(
            "No objects found",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }
    Text(
        "Found ${objects.size} object${if (objects.size == 1) "" else "s"}",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.size(8.dp))
    LazyColumn(
        modifier = Modifier.fillMaxWidth().heightIn(max = 170.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 4.dp),
    ) {
        items(objects.size) { index ->
            val obj = objects[index]
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.size(26.dp).clip(CircleShape)
                            .background(colorForClass(obj.classId)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("${index + 1}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.size(12.dp))
                    Text(obj.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    Text(
                        "${(obj.confidence * 100).roundToInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}