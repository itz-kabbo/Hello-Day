package com.flux.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flux.ui.foundation.FluxTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Named original ritual cues describing real moments:
 * Sunrise, Breathe, Hydrate, Commute, Focus, Exercise, Wind down.
 */
enum class RitualVisualCue(val label: String) {
    Sunrise("Sunrise"),
    Breathe("Breathe"),
    Hydrate("Hydrate"),
    Commute("Commute"),
    Focus("Focus"),
    Exercise("Exercise"),
    WindDown("Wind down")
}

/**
 * Compact cue scene for flat rows, cards, and selectors.
 * Rendered directly on canvas with no grey square containers.
 */
@Composable
fun RitualCue(
    cue: RitualVisualCue,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    useMorningWarmth: Boolean = false
) {
    val colors = FluxTheme.colors
    val primaryColor = if (useMorningWarmth) colors.warning else colors.accent
    val secondaryColor = if (useMorningWarmth) colors.warning.copy(alpha = 0.5f) else colors.secondaryInk

    Box(
        modifier = modifier
            .size(size)
            .semantics {
                this.contentDescription = cue.label
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            // Still resting composition (progress = 1.0f)
            drawCueScene(
                cue = cue,
                progress = 1.0f,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor
            )
        }
    }
}

/**
 * Centerpiece cue animation scene for RingingAlarmScreen.
 *
 * Runs once on alarm arrival (~4.5s organic gentle movement), then smoothly settles
 * into an almost-still resting state. If [reduceMotion] is true, shows the balanced
 * still composition immediately with zero entrance motion.
 *
 * Renders inside a quiet cue-specific field (warm sunrise surface) so it holds
 * presence without becoming a generic card.
 */
@Composable
fun RitualCueAnimation(
    cue: RitualVisualCue,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    reduceMotion: Boolean = FluxTheme.motion.reduceMotion
) {
    val colors = FluxTheme.colors
    val shapes = FluxTheme.shapes

    // Animate exactly once on arrival (0f -> 1f), then settle permanently
    val progress = remember { Animatable(if (reduceMotion) 1f else 0f) }

    LaunchedEffect(cue, reduceMotion) {
        if (!reduceMotion) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 4500, easing = LinearOutSlowInEasing)
            )
        } else {
            progress.snapTo(1f)
        }
    }

    val primaryColor = if (cue == RitualVisualCue.Sunrise || cue == RitualVisualCue.WindDown) {
        colors.warning
    } else {
        colors.accent
    }
    val secondaryColor = colors.secondaryInk

    // Quiet cue-specific field: soft morning warmth circular field providing presence without being a card
    Box(
        modifier = modifier
            .size(size)
            .clip(shapes.cueField)
            .background(colors.morningWarmth)
            .semantics {
                this.contentDescription = "Ritual cue scene: ${cue.label}"
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.75f)) {
            drawCueScene(
                cue = cue,
                progress = progress.value,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor
            )
        }
    }
}

/**
 * Renders the original vector artwork for each named cue scene.
 * Clean, solid geometric lines and forms with no gradients or decorative confetti.
 */
private fun DrawScope.drawCueScene(
    cue: RitualVisualCue,
    progress: Float,
    primaryColor: Color,
    secondaryColor: Color
) {
    val w = size.width
    val h = size.height
    val strokeWidth = (w * 0.055f).coerceAtLeast(1.8f)

    when (cue) {
        RitualVisualCue.Sunrise -> {
            // Calm horizon baseline
            val horizonY = h * 0.68f
            drawLine(
                color = secondaryColor.copy(alpha = 0.6f),
                start = Offset(w * 0.12f, horizonY),
                end = Offset(w * 0.88f, horizonY),
                strokeWidth = strokeWidth * 0.75f,
                cap = StrokeCap.Round
            )

            // Sun disk rising from below horizon to calm morning perch
            val sunRadius = w * 0.18f
            val startY = horizonY + sunRadius * 0.4f
            val targetY = h * 0.46f
            val currentSunY = startY - ((startY - targetY) * progress)

            drawCircle(
                color = primaryColor,
                radius = sunRadius,
                center = Offset(w * 0.5f, currentSunY)
            )

            // Delicate radiating dawn light beams (5 gentle rays)
            val rayLength = w * 0.12f * progress
            if (rayLength > 1f) {
                val angles = listOf(-60.0, -30.0, 0.0, 30.0, 60.0)
                for (angleDeg in angles) {
                    val rad = Math.toRadians(angleDeg - 90.0)
                    val rStart = sunRadius + w * 0.06f
                    val rEnd = rStart + rayLength
                    val startX = w * 0.5f + (rStart * cos(rad)).toFloat()
                    val startBeamY = currentSunY + (rStart * sin(rad)).toFloat()
                    val endX = w * 0.5f + (rEnd * cos(rad)).toFloat()
                    val endBeamY = currentSunY + (rEnd * sin(rad)).toFloat()

                    drawLine(
                        color = primaryColor.copy(alpha = 0.45f * progress),
                        start = Offset(startX, startBeamY),
                        end = Offset(endX, endBeamY),
                        strokeWidth = strokeWidth * 0.6f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        RitualVisualCue.Breathe -> {
            // Mindful breathing concentric circles expanding once and settling
            val center = Offset(w * 0.5f, h * 0.5f)
            val coreRadius = w * 0.14f

            // Inhale expands (0 -> 0.6), exhale settles (0.6 -> 1.0) into calm resting state (0.85 scale)
            val cycleProgress = if (progress <= 0.6f) {
                progress / 0.6f
            } else {
                1.0f - ((progress - 0.6f) / 0.4f) * 0.25f
            }

            // Outer expanding breath ring
            val outerRadius = coreRadius + (w * 0.26f * cycleProgress)
            drawCircle(
                color = primaryColor.copy(alpha = 0.25f + 0.15f * (1f - cycleProgress)),
                radius = outerRadius,
                center = center,
                style = Stroke(width = strokeWidth * 0.8f)
            )

            // Middle breath ring
            val midRadius = coreRadius + (w * 0.14f * cycleProgress)
            drawCircle(
                color = primaryColor.copy(alpha = 0.45f),
                radius = midRadius,
                center = center,
                style = Stroke(width = strokeWidth * 0.85f)
            )

            // Center tranquil breath core
            drawCircle(
                color = primaryColor,
                radius = coreRadius,
                center = center
            )
        }

        RitualVisualCue.Hydrate -> {
            // Minimalist water vessel / tumbler silhouette
            val glassTop = h * 0.22f
            val glassBottom = h * 0.82f
            val glassTopWidth = w * 0.48f
            val glassBottomWidth = w * 0.36f
            val centerX = w * 0.5f

            val glassPath = Path().apply {
                moveTo(centerX - glassTopWidth / 2f, glassTop)
                lineTo(centerX - glassBottomWidth / 2f, glassBottom)
                lineTo(centerX + glassBottomWidth / 2f, glassBottom)
                lineTo(centerX + glassTopWidth / 2f, glassTop)
            }

            drawPath(
                path = glassPath,
                color = secondaryColor.copy(alpha = 0.5f),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Water level smoothly rising and leveling to a calm flat meniscus
            val waterLevelProgress = (0.2f + 0.45f * progress).coerceIn(0f, 0.65f)
            val currentWaterY = glassBottom - (glassBottom - glassTop) * waterLevelProgress
            val currentWidth = glassBottomWidth + (glassTopWidth - glassBottomWidth) * waterLevelProgress

            // Gentle settling ripple: fades into flat calm meniscus as progress reaches 1f
            val rippleAmplitude = (1f - progress) * (h * 0.03f)
            val waterPath = Path().apply {
                moveTo(centerX - currentWidth / 2f + strokeWidth, currentWaterY)
                quadraticTo(
                    centerX,
                    currentWaterY + rippleAmplitude * sin(progress * PI.toFloat() * 3f),
                    centerX + currentWidth / 2f - strokeWidth,
                    currentWaterY
                )
                lineTo(centerX + glassBottomWidth / 2f - strokeWidth, glassBottom - strokeWidth)
                lineTo(centerX - glassBottomWidth / 2f + strokeWidth, glassBottom - strokeWidth)
                close()
            }

            drawPath(
                path = waterPath,
                color = primaryColor
            )
        }

        RitualVisualCue.Commute -> {
            // Serene pathway horizon with paced stride marks settling at arrival
            val pathY = h * 0.55f
            drawLine(
                color = secondaryColor.copy(alpha = 0.45f),
                start = Offset(w * 0.15f, pathY),
                end = Offset(w * 0.85f, pathY),
                strokeWidth = strokeWidth * 0.8f,
                cap = StrokeCap.Round
            )

            // Paced stride cadence marks advancing along the path
            val startX = w * 0.25f
            val endX = w * 0.72f
            val currentX = startX + (endX - startX) * progress

            // Trail dots
            val dotCount = 3
            for (i in 0 until dotCount) {
                val dotOffset = (i * w * 0.10f)
                val dotX = currentX - dotOffset
                if (dotX >= w * 0.20f) {
                    val alpha = (1.0f - (i * 0.28f)).coerceIn(0.2f, 1f)
                    drawCircle(
                        color = primaryColor.copy(alpha = alpha),
                        radius = (strokeWidth * 1.1f) * (1f - i * 0.18f),
                        center = Offset(dotX, pathY)
                    )
                }
            }

            // Destination resting ring
            drawCircle(
                color = secondaryColor.copy(alpha = 0.5f),
                radius = strokeWidth * 1.6f,
                center = Offset(endX, pathY),
                style = Stroke(width = strokeWidth * 0.6f)
            )
        }

        RitualVisualCue.Focus -> {
            // Concentric aperture marks converging smoothly into sharp focus alignment
            val center = Offset(w * 0.5f, h * 0.5f)
            val focusOffset = (w * 0.30f) - ((w * 0.12f) * progress)
            val reticleLength = w * 0.12f

            // 4 reticle corners
            // Top-left
            drawLine(
                color = primaryColor,
                start = Offset(center.x - focusOffset, center.y - focusOffset),
                end = Offset(center.x - focusOffset + reticleLength, center.y - focusOffset),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = primaryColor,
                start = Offset(center.x - focusOffset, center.y - focusOffset),
                end = Offset(center.x - focusOffset, center.y - focusOffset + reticleLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Top-right
            drawLine(
                color = primaryColor,
                start = Offset(center.x + focusOffset, center.y - focusOffset),
                end = Offset(center.x + focusOffset - reticleLength, center.y - focusOffset),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = primaryColor,
                start = Offset(center.x + focusOffset, center.y - focusOffset),
                end = Offset(center.x + focusOffset, center.y - focusOffset + reticleLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Bottom-left
            drawLine(
                color = primaryColor,
                start = Offset(center.x - focusOffset, center.y + focusOffset),
                end = Offset(center.x - focusOffset + reticleLength, center.y + focusOffset),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = primaryColor,
                start = Offset(center.x - focusOffset, center.y + focusOffset),
                end = Offset(center.x - focusOffset, center.y + focusOffset - reticleLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Bottom-right
            drawLine(
                color = primaryColor,
                start = Offset(center.x + focusOffset, center.y + focusOffset),
                end = Offset(center.x + focusOffset - reticleLength, center.y + focusOffset),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = primaryColor,
                start = Offset(center.x + focusOffset, center.y + focusOffset),
                end = Offset(center.x + focusOffset, center.y + focusOffset - reticleLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Center focus point
            drawCircle(
                color = primaryColor,
                radius = strokeWidth * 0.9f * progress,
                center = center
            )
        }

        RitualVisualCue.Exercise -> {
            // Dynamic uplifting arc trajectory line flexing upward and settling into poised readiness
            val startPoint = Offset(w * 0.18f, h * 0.72f)
            val endPoint = Offset(w * 0.82f, h * 0.32f)
            val controlPoint = Offset(
                w * 0.45f,
                (h * 0.78f) - (h * 0.42f * progress)
            )

            val arcPath = Path().apply {
                moveTo(startPoint.x, startPoint.y)
                quadraticTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y)
            }

            drawPath(
                path = arcPath,
                color = primaryColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Energy point at the crest
            drawCircle(
                color = primaryColor,
                radius = strokeWidth * 1.3f,
                center = Offset(endPoint.x, endPoint.y)
            )

            // Poise baseline anchor
            drawLine(
                color = secondaryColor.copy(alpha = 0.4f),
                start = Offset(w * 0.15f, h * 0.76f),
                end = Offset(w * 0.42f, h * 0.76f),
                strokeWidth = strokeWidth * 0.7f,
                cap = StrokeCap.Round
            )
        }

        RitualVisualCue.WindDown -> {
            // Delicate slender dusk crescent with gentle star-points settling into evening stillness
            val moonCenter = Offset(w * 0.46f, h * 0.48f)
            val moonRadius = w * 0.24f

            val moonPath = Path().apply {
                // Outer circle arc
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        center = moonCenter,
                        radius = moonRadius
                    ),
                    startAngleDegrees = -100f,
                    sweepAngleDegrees = 200f,
                    forceMoveTo = true
                )
                // Inner subtractive curve creating crescent
                quadraticTo(
                    moonCenter.x + moonRadius * 0.25f,
                    moonCenter.y,
                    moonCenter.x - (moonRadius * cos(Math.toRadians(100.0))).toFloat(),
                    moonCenter.y - (moonRadius * sin(Math.toRadians(100.0))).toFloat()
                )
                close()
            }

            drawPath(
                path = moonPath,
                color = primaryColor
            )

            // Celestial star points settling in
            val starAlpha = (0.3f + 0.7f * progress).coerceIn(0f, 1f)
            val star1 = Offset(w * 0.76f, h * 0.30f)
            val star2 = Offset(w * 0.68f, h * 0.68f)

            drawCrossStar(star1, size = w * 0.08f, color = primaryColor.copy(alpha = starAlpha))
            drawCrossStar(star2, size = w * 0.06f, color = secondaryColor.copy(alpha = starAlpha * 0.8f))
        }
    }
}

private fun DrawScope.drawCrossStar(center: Offset, size: Float, color: Color) {
    val half = size / 2f
    val stroke = (size * 0.18f).coerceAtLeast(1.2f)
    drawLine(
        color = color,
        start = Offset(center.x - half, center.y),
        end = Offset(center.x + half, center.y),
        strokeWidth = stroke,
        cap = StrokeCap.Round
    )
    drawLine(
        color = color,
        start = Offset(center.x, center.y - half),
        end = Offset(center.x, center.y + half),
        strokeWidth = stroke,
        cap = StrokeCap.Round
    )
}
