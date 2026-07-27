package com.percepta.app.detection

import com.percepta.app.CocoLabels
import kotlin.math.max

private const val NUM_CANDIDATES = 8400
private const val NUM_CLASSES = 80
private const val BOX_ATTRS = 4

fun decodeDetections(
    output: FloatArray,
    transform: LetterboxTransform,
    confidenceTreshold: Float = 0.25f,
    iouThreshold: Float = 0.45f,
    maxDetections: Int = 100,
): List<DetectedObject> {
    val contentWidth = MODEL_INPUT_SIZE - 2 * transform.padX
    val contentHeight = MODEL_INPUT_SIZE - 2 * transform.padY
    val candidates = mutableListOf<DetectedObject>()

    for (i in 0 until NUM_CANDIDATES) {
        var bestClass = 0
        var bestScore = 0f
        for (c in 0 until NUM_CLASSES) {
            val score = output[(BOX_ATTRS + c) * NUM_CANDIDATES + i]
            if (score > bestScore) {
                bestScore = score
                bestClass = c
            }
        }
        if (bestScore < confidenceTreshold) continue
        val centerX = output[0 * NUM_CANDIDATES + i]
        val centerY = output[1 * NUM_CANDIDATES + i]
        val width = output[2 * NUM_CANDIDATES + i]
        val height = output[3 * NUM_CANDIDATES + i]

        val left = (centerX - width / 2 - transform.padX) / contentWidth
        val top = (centerY - height / 2 - transform.padY) / contentHeight
        val right = (centerX + width / 2 - transform.padX) / contentWidth
        val bottom = (centerY + height / 2 - transform.padY) / contentHeight

        candidates += DetectedObject(
            classId = bestClass,
            label = CocoLabels[bestClass],
            confidence = bestScore,
            boundingBox = BoundingBox(
                left = left.coerceIn(0f, 1f),
                top = top.coerceIn(0f, 1f),
                right = right.coerceIn(0f, 1f),
                bottom = bottom.coerceIn(0f, 1f),
            ),
        )
    }
    return nonMaxSuppression(candidates, iouThreshold, maxDetections)
}

fun nonMaxSuppression(
    candidates: MutableList<DetectedObject>,
    iouThreshold: Float,
    maxDetections: Int
): List<DetectedObject> {
    val kept = mutableListOf<DetectedObject>()

    for (candidate in candidates.sortedByDescending { it.confidence }) {
        if (kept.size >= maxDetections) break

        val isDuplicate = kept.any { exist ->
            exist.classId == candidate.classId && interSectionOverUnion(exist.boundingBox, candidate.boundingBox) > iouThreshold
        }
        if (!isDuplicate) kept += candidate
    }
    return kept
}

fun interSectionOverUnion(
    existBoundingBox: BoundingBox,
    candidateBoundingBox: BoundingBox
): Float {
    val interLeft = max(existBoundingBox.left, candidateBoundingBox.left)
    val interTop = max(existBoundingBox.top, candidateBoundingBox.top)
    val interRight = max(existBoundingBox.right, candidateBoundingBox.right)
    val interBottom = max(existBoundingBox.bottom, candidateBoundingBox.bottom)

    val interWidth = (interRight - interLeft).coerceAtLeast(0f)
    val interHeight = (interBottom - interTop).coerceAtLeast(0f)

    val interSection = interWidth * interHeight
    val union = existBoundingBox.width * existBoundingBox.height + candidateBoundingBox.width * candidateBoundingBox.height - interSection

    return if (union <= 0f) 0f else interSection / union
}
