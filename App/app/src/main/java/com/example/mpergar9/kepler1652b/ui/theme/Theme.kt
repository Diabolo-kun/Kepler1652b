package com.example.mpergar9.kepler1652b.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KeplerColorScheme = darkColorScheme(
    primary = CyanCurvature,
    onPrimary = VoidBlack,
    secondary = NeonCyan,
    onSecondary = VoidBlack,
    tertiary = WarningAmber,
    background = VoidBlack,
    surface = DeepSpaceBlue,
    surfaceVariant = HullGray,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = PanelBorder,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun Kepler1652bTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KeplerColorScheme,
        typography = Typography,
        content = content
    )
}