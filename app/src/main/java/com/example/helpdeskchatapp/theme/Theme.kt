package com.example.helpdeskchatapp.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AndroidGreen,
    onPrimary = OnGreen,
    primaryContainer = AndroidGreenContainer,
    onPrimaryContainer = TextPrimary,
    secondary = AndroidGreenDark,
    onSecondary = OnGreen,
    background = NeutralBackground,
    surface = NeutralSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = TextSecondary,
)

private val DarkColorScheme = darkColorScheme(
    primary = AndroidGreen,
    onPrimary = TextPrimary,
    primaryContainer = AndroidGreenDark,
    onPrimaryContainer = OnGreen,
    secondary = AndroidGreenDark,
    onSecondary = OnGreen,
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
