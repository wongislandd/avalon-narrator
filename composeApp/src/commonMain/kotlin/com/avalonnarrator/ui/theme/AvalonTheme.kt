package com.avalonnarrator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF8B6B2E),
    onPrimary = Color(0xFFFFF8E8),
    secondary = Color(0xFF3F2A14),
    onSecondary = Color(0xFFFFF3D6),
    background = Color(0xFFF5E9CF),
    onBackground = Color(0xFF21180F),
    surface = Color(0xFFEEDBB7),
    onSurface = Color(0xFF2A1E12),
    error = Color(0xFF9C2F2F),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE0B76A),
    onPrimary = Color(0xFF332207),
    secondary = Color(0xFFF0D6A0),
    onSecondary = Color(0xFF1D1409),
    background = Color(0xFF1C140B),
    onBackground = Color(0xFFF6E7C6),
    surface = Color(0xFF302112),
    onSurface = Color(0xFFF3E0B2),
    error = Color(0xFFFFA7A7),
)

@Composable
fun AvalonTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        content = content,
    )
}
