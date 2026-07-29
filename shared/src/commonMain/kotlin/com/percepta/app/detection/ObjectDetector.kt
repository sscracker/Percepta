package com.percepta.app.detection

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import percepta.shared.generated.resources.Res

class ObjectDetector(private val session: OnnxSession): AutoCloseable {
    suspend fun detect(image: ImageBitmap): List<DetectedObject> =
        withContext(Dispatchers.Default) {
            val result = preprocess(image)
            val rawOutput = session.run(result.tensor)
            decodeDetections(rawOutput, result.transform)
        }

    override fun close() = session.close()
}

suspend fun createObjectDetector(): ObjectDetector {
    val modelBytes = Res.readBytes("files/yolov8n.onnx")
    val session = createOnnxSession(modelBytes)
    return ObjectDetector(session)
}