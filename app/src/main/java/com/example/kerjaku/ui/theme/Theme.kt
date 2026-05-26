package com.example.pamfinal.ui.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MinimalistColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    background = WhiteBackground,
    surface = GraySurface,
    onPrimary = WhiteBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun Kerjaku(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MinimalistColorScheme,
        typography = Typography,
        content = content
    )
}