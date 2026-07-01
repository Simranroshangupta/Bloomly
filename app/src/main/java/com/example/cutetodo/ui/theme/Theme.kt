package com.example.cutetodo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBackground,
    onSecondary = DarkBackground,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    secondary = SoftPink,
    background = BackgroundColorLight,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color(0xFF5C4A72),
    onBackground = Color(0xFF5C4A72),
    onSurface = Color(0xFF5C4A72),
    onSurfaceVariant = Color(0xFF8C7A9C)
)

@Composable
fun CuteTodoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
