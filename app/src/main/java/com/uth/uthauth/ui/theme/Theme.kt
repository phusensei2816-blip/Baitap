package com.uth.uthauth.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = UthBlue,
    onPrimary = Color.White,
    primaryContainer = UthBlueSoft,
    onPrimaryContainer = UthBlue,
    background = ScreenBackground,
    onBackground = TextPrimary,
    surface = Color.White,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = OutlineSoft,
    error = DangerRed,
)

@Composable
fun UthAuthTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
