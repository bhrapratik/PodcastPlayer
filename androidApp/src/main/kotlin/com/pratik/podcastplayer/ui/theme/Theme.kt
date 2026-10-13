
package com.pratik.podcastplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PodcastPurple,
    onPrimary = Color.White,
    primaryContainer = PodcastPurpleLight,
    onPrimaryContainer = Color(0xFF25104F),

    secondary = Color(0xFF625B71),
    onSecondary = Color.White,

    background = LightBackground,
    onBackground = LightText,

    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = Color(0xFFF0ECF7),
    onSurfaceVariant = LightTextSecondary,

    outline = LightOutline,
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = PodcastLavender,
    onPrimary = Color(0xFF27105F),
    primaryContainer = Color(0xFF49318A),
    onPrimaryContainer = Color(0xFFE9DDFF),

    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),

    background = DarkBackground,
    onBackground = DarkText,

    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,

    outline = DarkOutline,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun PodcastPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = PodcastTypography,
        content = content
    )
}
