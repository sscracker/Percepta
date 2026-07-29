package com.percepta.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

@Composable
actual fun rememberImagePickerAction(onImagePicked: (ImageBitmap) -> Unit): () -> Unit {
    TODO("Not yet implemented")
}

@Composable
actual fun rememberCameraCaptureAction(onImageCaptured: (ImageBitmap) -> Unit): () -> Unit {
    TODO("Not yet implemented")
}