package com.example.matemath.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Minimalist Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = MateMathColors.Primary,
    onPrimary = MateMathColors.Surface,
    primaryContainer = MateMathColors.PrimaryLight,
    onPrimaryContainer = MateMathColors.OnSurface,
    
    secondary = MateMathColors.OnSurfaceSecondary,
    onSecondary = MateMathColors.Surface,
    secondaryContainer = MateMathColors.SurfaceVariant,
    onSecondaryContainer = MateMathColors.OnSurface,
    
    tertiary = MateMathColors.OnSurfaceTertiary,
    onTertiary = MateMathColors.Surface,
    tertiaryContainer = MateMathColors.SurfaceVariant,
    onTertiaryContainer = MateMathColors.OnSurface,
    
    error = MateMathColors.Error,
    onError = MateMathColors.Surface,
    errorContainer = MateMathColors.ErrorLight,
    onErrorContainer = MateMathColors.Error,
    
    background = MateMathColors.Background,
    onBackground = MateMathColors.OnSurface,
    
    surface = MateMathColors.Surface,
    onSurface = MateMathColors.OnSurface,
    surfaceVariant = MateMathColors.SurfaceVariant,
    onSurfaceVariant = MateMathColors.OnSurfaceSecondary,
    
    outline = MateMathColors.OnSurfaceTertiary,
    outlineVariant = MateMathColors.ProgressBackground,
    
    scrim = MateMathColors.Overlay,
    
    inverseSurface = MateMathColors.OnSurface,
    inverseOnSurface = MateMathColors.Surface,
    inversePrimary = MateMathColors.PrimaryLight
)

// Dark mode (if needed later, but focusing on light mode for children)
private val DarkColorScheme = darkColorScheme(
    primary = MateMathColors.PrimaryLight,
    secondary = MateMathColors.OnSurfaceTertiary,
    tertiary = MateMathColors.OnSurfaceSecondary,
    background = MateMathColors.OnSurface,
    surface = MateMathColors.OnSurface,
    onPrimary = MateMathColors.OnSurface,
    onSecondary = MateMathColors.Surface,
    onTertiary = MateMathColors.Surface,
    onBackground = MateMathColors.Surface,
    onSurface = MateMathColors.Surface,
)

@Composable
fun MateMathTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled for consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MateMathColors.Background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MateMathTypography,
        content = content
    )
}