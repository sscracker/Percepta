package com.percepta.app.detection

import kotlinx.serialization.Serializable

@Serializable
data class DetectedObject(
    val classId: Int,
    val label: String,
    val confidence: Float,
    val boundingBox: BoundingBox
)

@Serializable
data class BoundingBox(
    val left: Float,
    val right: Float,
    val top: Float,
    val bottom: Float
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top
}