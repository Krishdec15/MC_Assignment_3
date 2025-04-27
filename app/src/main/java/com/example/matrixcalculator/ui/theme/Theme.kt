package com.example.matrixcalculator.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Modern blue color scheme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3F51B5),           // Indigo
    secondary = Color(0xFF03A9F4),         // Light Blue
    tertiary = Color(0xFF673AB7),          // Deep Purple
    background = Color(0xFF121212),        // Dark background
    surface = Color(0xFF212121),           // Dark surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color(0xFFCF6679)              // Error color for dark theme
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3F51B5),           // Indigo
    secondary = Color(0xFF03A9F4),         // Light Blue
    tertiary = Color(0xFF673AB7),          // Deep Purple
    background = Color.White,
    surface = Color(0xFFF8F8F8),           // Light gray surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    error = Color(0xFFB00020)              // Error color for light theme
)

@Composable
fun MatrixCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}