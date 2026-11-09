package com.example.keinvitaciones.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.White,
    primaryContainer = SoftPink,
    onPrimaryContainer = DarkCharcoal,
    secondary = DarkCharcoal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0EBE1),
    onSecondaryContainer = DarkCharcoal,
    tertiary = GoldDark,
    onTertiary = Color.White,
    background = CreamBackground,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF9F5EC),
    onSurfaceVariant = DarkCharcoal,
    outline = CardBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = GoldLight,
    onPrimary = DarkCharcoal,
    primaryContainer = Color(0xFF332715),
    onPrimaryContainer = GoldLight,
    secondary = Color(0xFFE0D7C9),
    onSecondary = DarkCharcoal,
    background = Color(0xFF141312),
    onBackground = Color(0xFFEBE6DF),
    surface = Color(0xFF1E1D1B),
    onSurface = Color(0xFFEBE6DF),
    surfaceVariant = Color(0xFF2B2824),
    onSurfaceVariant = Color(0xFFE0D7C9),
    outline = Color(0xFF4A443A)
)

@Composable
fun KEInvitacionesTheme(
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
