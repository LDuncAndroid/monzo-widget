package com.emmav.monzo.widget.common

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AppColors = lightColors(
    primary = Color(0xFF3F51B5),
    onPrimary = Color.White,
    secondary = Color(0xFF303F9F),
    onSecondary = Color.Red,
    surface = Color(0xFFFF5722),
    onSurface = Color.Gray
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = AppColors,
        content = content
    )
}