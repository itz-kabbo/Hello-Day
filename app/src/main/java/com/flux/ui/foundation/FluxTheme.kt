package com.flux.ui.foundation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Accessor object for Flux design tokens.
 */
object FluxTheme {
    val colors: FluxColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalFluxColors.current

    val typography: FluxTypeScale
        @Composable
        @ReadOnlyComposable
        get() = LocalFluxTypography.current

    val spacing: FluxSpacingTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalFluxSpacing.current

    val shapes: FluxShapeTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalFluxShapes.current

    val motion: FluxMotionTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalFluxMotion.current
}

/**
 * Bridges Flux tokens to Material3 ColorScheme so Material accessibility
 * and platform interaction behavior render with Flux colors.
 */
private fun FluxColorPalette.toMaterialColorScheme(): ColorScheme {
    return if (isDark) {
        darkColorScheme(
            primary = accent,
            onPrimary = primaryInk,
            primaryContainer = accentSoft,
            onPrimaryContainer = primaryInk,
            background = canvas,
            onBackground = primaryInk,
            surface = surface,
            onSurface = primaryInk,
            surfaceVariant = surfaceSubtle,
            onSurfaceVariant = secondaryInk,
            outline = divider,
            outlineVariant = divider,
            error = destructive,
            onError = canvas
        )
    } else {
        lightColorScheme(
            primary = accent,
            onPrimary = surface,
            primaryContainer = accentSoft,
            onPrimaryContainer = accent,
            background = canvas,
            onBackground = primaryInk,
            surface = surface,
            onSurface = primaryInk,
            surfaceVariant = surfaceSubtle,
            onSurfaceVariant = secondaryInk,
            outline = divider,
            outlineVariant = divider,
            error = destructive,
            onError = surface
        )
    }
}

@Composable
fun FluxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorPalette = if (darkTheme) FluxDarkColors else FluxLightColors
    val motionTokens = DefaultFluxMotion.copy(reduceMotion = reduceMotion)

    CompositionLocalProvider(
        LocalFluxColors provides colorPalette,
        LocalFluxTypography provides DefaultFluxTypography,
        LocalFluxSpacing provides DefaultFluxSpacing,
        LocalFluxShapes provides DefaultFluxShapes,
        LocalFluxMotion provides motionTokens
    ) {
        MaterialTheme(
            colorScheme = colorPalette.toMaterialColorScheme(),
            content = content
        )
    }
}
