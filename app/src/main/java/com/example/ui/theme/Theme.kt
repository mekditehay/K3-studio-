package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val StudioColorScheme = darkColorScheme(
    primary = StudioCyan,
    onPrimary = Color.Black,
    primaryContainer = StudioCyanDark,
    onPrimaryContainer = Color.White,
    secondary = StudioGold,
    onSecondary = Color.Black,
    secondaryContainer = StudioGoldDark,
    onSecondaryContainer = Color.White,
    tertiary = StudioPurple,
    onTertiary = Color.White,
    background = StudioObsidian,
    onBackground = TextWhite,
    surface = StudioDarkSurface,
    onSurface = TextWhite,
    surfaceVariant = StudioCardBg,
    onSurfaceVariant = TextGrayLight,
    outline = StudioBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = StudioObsidian.toArgb()
            window.navigationBarColor = StudioObsidian.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = StudioColorScheme,
        typography = Typography,
        content = content
    )
}
