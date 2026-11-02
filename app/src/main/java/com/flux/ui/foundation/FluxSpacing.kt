package com.flux.ui.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Canonical Flux spacing tokens based on the 4dp unit grid.
 */
@Immutable
data class FluxSpacingTokens(
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val gutter: Dp = 20.dp,
    val minInteractiveTarget: Dp = 48.dp
)

val DefaultFluxSpacing = FluxSpacingTokens()

val LocalFluxSpacing = staticCompositionLocalOf { DefaultFluxSpacing }
