package com.percepta.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.percepta.app.ui.DetectionScreen
import com.percepta.app.ui.PerceptaTheme

@Composable
@Preview
fun App() {
    PerceptaTheme {
        DetectionScreen()
    }
}