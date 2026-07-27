package com.percepta.app.detection

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import kotlin.math.min
import kotlin.math.roundToInt

const val MODEL_INPUT_SIZE = 640

private const val PAD_VALUE = 114f/255f

data class LetterboxTransform(
    val scale: Float,
    val padX: Float,
    val padY: Float,
)

class PreprocessResult(
    val tensor: FloatArray,
    val transform: LetterboxTransform,
)

fun preprocess(image: ImageBitmap): PreprocessResult {
    val pixels = image.toPixelMap()
    val srcWidth = image.width
    val srcHeight = image.height

    val scale = min(MODEL_INPUT_SIZE.toFloat() / srcWidth, MODEL_INPUT_SIZE.toFloat() / srcHeight)
    val scaledWidth = (srcWidth * scale).roundToInt()
    val scaledHeight = (srcHeight * scale).roundToInt()

    val padX = (MODEL_INPUT_SIZE - scaledWidth) / 2f
    val padY = (MODEL_INPUT_SIZE - scaledHeight) / 2f
    val channelSize = MODEL_INPUT_SIZE * MODEL_INPUT_SIZE

    val tensor = FloatArray(3 * channelSize) { PAD_VALUE }

    val offsetX = padX.toInt()
    val offsetY = padY.toInt()

    for (y in 0 until scaledHeight) {
        val srcY = (y / scale).toInt().coerceIn(0, srcHeight - 1)
        val dstRow = (y + offsetY) * MODEL_INPUT_SIZE
        for (x in 0 until scaledWidth) {
            val srcX = (x / scale).toInt().coerceIn(0, srcWidth - 1)
            val color = pixels[srcX, srcY]
            val i = dstRow + x + offsetX

            tensor[i] = color.red
            tensor[channelSize + i] = color.green
            tensor[2 * channelSize + i] = color.blue
        }
    }
    return PreprocessResult(tensor, LetterboxTransform(scale, padX, padY))
}