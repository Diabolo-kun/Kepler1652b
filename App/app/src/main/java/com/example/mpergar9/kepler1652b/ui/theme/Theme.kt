package com.example.mpergar9.kepler1652b.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = StatusBarColor.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = KeplerColorScheme,
        typography = Typography,
        content = content
    )
}