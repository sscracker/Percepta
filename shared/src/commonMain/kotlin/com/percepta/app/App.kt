package com.percepta.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.percepta.app.ui.DetectionScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        DetectionScreen()
    }
}