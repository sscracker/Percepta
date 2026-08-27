package com.percepta.app.ui

import androidx.compose.ui.graphics.Color

private val boxPalette = listOf(
    Color(0xFFEF5350), Color(0xFF42A5F5), Color(0xFF66BB6A), Color(0xFFFFA726),
    Color(0xFFAB47BC), Color(0xFF26C6DA), Color(0xFFFFCA28), Color(0xFFEC407A),
    Color(0xFF7E57C2), Color(0xFF9CCC65),
)

fun colorForClass(classId: Int): Color = boxPalette[classId % boxPalette.size]
