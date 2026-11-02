package com.flux.ui.foundation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Shared motion system for Flux:
 * - 150-200ms for control states (buttons, toggles, selectors)
 * - 250-300ms for reveals (sheets, dialogs, crossfades)
 * - 450ms exclusively for ritual-row-to-detail continuity
 * - Respects Reduce Motion with short 150ms fades and still cues
 */
@Immutable
data class FluxMotionTokens(
    val controlDurationMs: Int = 180,
    val revealDurationMs: Int = 280,
    val continuityDurationMs: Int = 450,
    val reduceMotionFadeMs: Int = 150,

    // Aliases for component compatibility
    val pressDurationMs: Int = 180,
    val selectionDurationMs: Int = 180,
    val sheetDurationMs: Int = 280,
    val layoutDurationMs: Int = 280,
    val sharedDetailDurationMs: Int = 450,
    val alarmTransitionMs: Int = 450,

    val standardCurve: Easing = FastOutSlowInEasing,
    val enteringDecelCurve: Easing = LinearOutSlowInEasing,
    val exitingAccelCurve: Easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f),
    val emphasizedCurve: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f),
    val reduceMotion: Boolean = false
) {
    /**
     * Resolves duration taking user accessibility [reduceMotion] preference into account.
     * In reduced motion mode, yields a short 150ms fade or immediate transition.
     */
    fun resolveDuration(standardMs: Int): Int {
        return if (reduceMotion) reduceMotionFadeMs else standardMs
    }

    fun durationForControl(): Int {
        return if (reduceMotion) 0 else controlDurationMs
    }

    fun durationForReveal(): Int {
        return if (reduceMotion) reduceMotionFadeMs else revealDurationMs
    }

    fun durationForContinuity(): Int {
        return if (reduceMotion) reduceMotionFadeMs else continuityDurationMs
    }
}

val DefaultFluxMotion = FluxMotionTokens()

val LocalFluxMotion = staticCompositionLocalOf { DefaultFluxMotion }
