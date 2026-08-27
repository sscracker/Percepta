package com.percepta.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF5B5FEF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3E4FF),
    onPrimaryContainer = Color(0xFF14166B),
    secondary = Color(0xFF5B5D72),
    background = Color(0xFFFBFBFF),
    onBackground = Color(0xFF1B1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFE9E7F2),
    onSurfaceVariant = Color(0xFF48465A),
    error = Color(0xFFBA1A1A),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFAEB0FF),
    onPrimary = Color(0xFF1E2080),
    primaryContainer = Color(0xFF383AA6),
    onPrimaryContainer = Color(0xFFE3E4FF),
    secondary = Color(0xFFC5C4DD),
    background = Color(0xFF121216),
    onBackground = Color(0xFFE4E1E9),
    surface = Color(0xFF1B1B20),
    onSurface = Color(0xFFE4E1E9),
    surfaceVariant = Color(0xFF48465A),
    onSurfaceVariant = Color(0xFFCAC5D6),
    error = Color(0xFFFFB4AB),
)

@Composable
fun PerceptaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
