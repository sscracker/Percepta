package com.percepta.app.detection

expect class OnnxSession: AutoCloseable {
    fun run (input: FloatArray): FloatArray
    override fun close()
}

expect fun createOnnxSession(modelBytes: ByteArray): OnnxSession