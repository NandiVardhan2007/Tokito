package com.owlcoders.tokito.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

/**
 * Material components (dialogs, text fields, the LinkGuard screen) read the colour scheme, so it
 * is derived from the same tokens. surfaceTint matches the surface: Material otherwise mixes the
 * accent into every elevated container.
 */
private fun TokitoColors.toScheme(): ColorScheme {
    val base = if (isDark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = accentFill,
        onPrimary = onAccent,
        primaryContainer = accentWash,
        onPrimaryContainer = accent,
        secondary = accent,
        onSecondary = onAccent,
        secondaryContainer = fill,
        onSecondaryContainer = textHigh,
        tertiary = info,
        background = background,
        onBackground = textHigh,
        surface = surface,
        onSurface = textHigh,
        surfaceVariant = surfaceRaised,
        onSurfaceVariant = textMid,
        surfaceContainerLowest = surface,
        surfaceContainerLow = surface,
        surfaceContainer = surface,
        surfaceContainerHigh = surface,
        surfaceContainerHighest = surfaceRaised,
        surfaceTint = surface,
        outline = separator,
        outlineVariant = separator,
        error = danger,
        onError = onAccent,
        scrim = scrim
    )
}

/** Corner scale. Nested corners are derived with concentricRadius(), never picked by eye. */
val TokitoShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun TokitoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkTokitoColors else LightTokitoColors
    CompositionLocalProvider(LocalTokitoColors provides colors) {
        MaterialTheme(
            colorScheme = colors.toScheme(),
            typography = TokitoTypography,
            shapes = TokitoShapes,
            content = content
        )
    }
}
