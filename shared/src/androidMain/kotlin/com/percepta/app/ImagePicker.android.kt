package com.percepta.app

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun rememberImagePickerAction(onImagePicked: (ImageBitmap) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) {
        uri -> uri ?: return@rememberLauncherForActivityResult
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@rememberLauncherForActivityResult
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        onImagePicked(bitmap.asImageBitmap())
    }
    return {
        launcher.launch(
        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
    )
    }
}

@Composable
actual fun rememberCameraCaptureAction(onImageCaptured: (ImageBitmap) -> Unit): () -> Unit {
    val context = LocalContext.current
    val photoFile = remember {
        File.createTempFile("capture", ".jpg", context.cacheDir)
    }
    val photoUri = remember {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) {
            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            onImageCaptured(bitmap.asImageBitmap())
        }
    }
    return {
        launcher.launch(photoUri)
    }
}