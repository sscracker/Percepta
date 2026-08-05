package com.percepta.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
actual fun rememberImagePickerAction(onImagePicked: (ImageBitmap) -> Unit): () -> Unit {
    return {
        val chooser = JFileChooser().apply {
            fileFilter = FileNameExtensionFilter("Images", "jpg", "jpeg", "png")
        }
        val result = chooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val bytes = chooser.selectedFile.readBytes()
            onImagePicked(bytes.decodeToImageBitmap())
        }
    }
}

@Composable
actual fun rememberCameraCaptureAction(onImageCaptured: (ImageBitmap) -> Unit): () -> Unit {
    return  {
    }
}