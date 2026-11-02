package com.flux.ui.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Canonical Flux semantic color tokens matching the Soft Utility specification.
 */
@Immutable
data class FluxColorPalette(
    val canvas: Color,
    val surface: Color,
    val surfaceSubtle: Color,
    val primaryInk: Color,
    val secondaryInk: Color,
    val divider: Color,
    val accent: Color,
    val accentSoft: Color,
    val success: Color,
    val warning: Color,
    val destructive: Color,
    val morningWarmth: Color,
    val isDark: Boolean
)

val FluxLightColors = FluxColorPalette(
    canvas = Color(0xFFF7F8FC),
    surface = Color(0xFFFFFFFF),
    surfaceSubtle = Color(0xFFF0F2F7),
    primaryInk = Color(0xFF161A22),
    secondaryInk = Color(0xFF657083),
    divider = Color(0xFFE1E5EC),
    accent = Color(0xFF3266E8),
    accentSoft = Color(0xFFEAF0FF),
    success = Color(0xFF197A45),
    warning = Color(0xFFA85C00),
    destructive = Color(0xFFC53731),
    morningWarmth = Color(0xFFFFF5E8),
    isDark = false
)

val FluxDarkColors = FluxColorPalette(
    canvas = Color(0xFF101216),
    surface = Color(0xFF181B20),
    surfaceSubtle = Color(0xFF22262D),
    primaryInk = Color(0xFFF4F6FA),
    secondaryInk = Color(0xFFA9B2C2),
    divider = Color(0xFF303640),
    accent = Color(0xFF8DACFF),
    accentSoft = Color(0xFF1D315C),
    success = Color(0xFF65D69B),
    warning = Color(0xFFF3B35A),
    destructive = Color(0xFFFF938C),
    morningWarmth = Color(0xFF31271D),
    isDark = true
)

val LocalFluxColors = staticCompositionLocalOf { FluxLightColors }
