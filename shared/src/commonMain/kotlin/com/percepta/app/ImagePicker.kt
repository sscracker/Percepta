package com.percepta.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun rememberImagePickerAction(onImagePicked: (ImageBitmap) -> Unit) : () -> Unit

@Composable
expect fun rememberCameraCaptureAction(onImageCaptured: (ImageBitmap) -> Unit): () -> Unit