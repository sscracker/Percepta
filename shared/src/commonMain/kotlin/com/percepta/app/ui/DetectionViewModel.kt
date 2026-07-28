package com.percepta.app.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.percepta.app.detection.ObjectDetector
import com.percepta.app.detection.createObjectDetector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetectionViewModel: ViewModel() {

    private val _uiState = MutableStateFlow<DetectionUiState>(DetectionUiState.Idle)
    val uiState: StateFlow<DetectionUiState> = _uiState.asStateFlow()

    private var detector: ObjectDetector? = null

    fun onImageSelected(image: ImageBitmap) {
        _uiState.value = DetectionUiState.Loading(image)
       viewModelScope.launch {
           try {
               val activeDetector = detector ?: createObjectDetector().also { detector = it }
               val objects = activeDetector.detect(image)
               _uiState.value = DetectionUiState.Success(image, objects)
           } catch (e: Exception) {
               _uiState.value = DetectionUiState.Error(e.message ?: "Detection failed")
           }
       }
    }

    override fun onCleared() {
        detector?.close()
        super.onCleared()
    }
}