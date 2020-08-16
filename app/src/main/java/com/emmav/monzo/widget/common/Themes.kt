package com.emmav.monzo.widget.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Indigo300 = Color(0xFF7986CB)
private val Indigo700 = Color(0xFF303F9F)
private val Indigo900 = Color(0xFF1A237E)
private val DeepOrange500 = Color(0xFFFF5722)

private val AppLightColors = lightColors(
    primary = Indigo700,
    primaryVariant = Indigo900,
    onPrimary = Color.White,
    secondary = DeepOrange500,
    onSecondary = Color.Black
)

private val AppDarkColors = darkColors(
    primary = Indigo300,
    primaryVariant = Indigo700,
    onPrimary = Color.Black,
    secondary = DeepOrange500,
    onSecondary = Color.White,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) AppDarkColors else AppLightColors,
        content = content
    )
}