package com.percepta.app.detection

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.nio.FloatBuffer

private const val INPUT_WIDTH = 640L
private const val INPUT_HEIGHT = 640L
private const val INPUT_CHANNELS = 3L

actual class OnnxSession(
    private val environment: OrtEnvironment,
    private val session: OrtSession,
): AutoCloseable {

    actual fun run(input: FloatArray): FloatArray {
        val shape = longArrayOf(1, INPUT_CHANNELS, INPUT_HEIGHT, INPUT_WIDTH)
        return OnnxTensor.createTensor(environment, FloatBuffer.wrap(input), shape).use { tensor ->
            val inputName = session.inputNames.first()
            session.run(mapOf(inputName to tensor)).use { result ->
                @Suppress("UNCHECKED_CAST")
                val output = result[0].value as Array<Array<FloatArray>>
                output[0].flatMap { row ->
                    row.asIterable()
                }.toFloatArray()
            }
        }
    }

    actual override fun close() {
        session.close()
    }
}

actual fun createOnnxSession(modelBytes: ByteArray): OnnxSession {
    val environment = OrtEnvironment.getEnvironment()
    val session = environment.createSession(modelBytes, OrtSession.SessionOptions())
    return OnnxSession(environment, session)
}