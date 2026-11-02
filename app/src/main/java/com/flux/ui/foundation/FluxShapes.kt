package com.flux.ui.foundation

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

/**
 * Canonical Flux shapes:
 * - Controls: 12dp
 * - Sole elevated ritual surface: 16dp
 * - Bottom sheets: 24dp top corners
 */
@Immutable
data class FluxShapeTokens(
    val control: CornerBasedShape = RoundedCornerShape(12.dp),
    val soleElevatedSurface: CornerBasedShape = RoundedCornerShape(16.dp),
    val grouped: CornerBasedShape = RoundedCornerShape(16.dp),
    val bottomSheet: CornerBasedShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    val cueField: CornerBasedShape = RoundedCornerShape(24.dp),
    val smallControl: CornerBasedShape = RoundedCornerShape(8.dp)
)

val DefaultFluxShapes = FluxShapeTokens()

val LocalFluxShapes = staticCompositionLocalOf { DefaultFluxShapes }
