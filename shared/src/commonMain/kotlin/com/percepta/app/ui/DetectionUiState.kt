package com.percepta.app.ui

import androidx.compose.ui.graphics.ImageBitmap
import com.percepta.app.detection.DetectedObject

sealed interface DetectionUiState {
    data object Idle: DetectionUiState
    data class Loading(val imageBitmap: ImageBitmap): DetectionUiState
    data class Success(
        val imageBitmap: ImageBitmap,
        val objects: List<DetectedObject>
    ): DetectionUiState
    data class Error(val message: String): DetectionUiState
}