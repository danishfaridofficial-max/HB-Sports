package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryRed,
    secondary = AccentRed,
    tertiary = YellowNeon,
    background = DarkBlueBackground,
    surface = DarkBlueBackground,
    surfaceVariant = CardDark,
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = White,
    onSurfaceVariant = LightGray
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryRed,
    secondary = AccentRed,
    tertiary = YellowNeon,
    background = Color(0xFFF5F5F7),
    surface = Color.White,
    surfaceVariant = Color(0xFFEEEEF4),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = StadiumBlack,
    onSurface = StadiumBlack,
    onSurfaceVariant = Color(0xFF33333F)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark theme for premium stadium vibe
    dynamicColor: Boolean = false, // Use our handcrafted branding colors
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
