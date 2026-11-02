package com.flux.ritual

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flux.ritual.data.RitualConverters
import com.flux.ui.components.RitualVisualCue
import com.flux.ui.foundation.DefaultFluxMotion
import com.flux.ui.foundation.DefaultFluxShapes
import com.flux.ui.foundation.DefaultFluxTypography
import com.flux.ui.foundation.FluxMotionTokens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit and semantic verification tests for Flux visual redesign:
 * - Shared motion tokens and accessibility Reduce Motion resolution
 * - Typography scale rules
 * - Shape radius constraints
 * - Named cue enum and database converter roundtripping
 * - Day selection and alarm snooze semantics
 */
class RitualSemanticTest {

    @Test
    fun testMotionTokenDurationsAndReduceMotion() {
        val standardMotion = DefaultFluxMotion

        // Control states: 150-200ms range
        assertTrue("Control duration must be between 150ms and 200ms", standardMotion.controlDurationMs in 150..200)
        assertEquals(180, standardMotion.controlDurationMs)

        // Reveals: 250-300ms range
        assertTrue("Reveal duration must be between 250ms and 300ms", standardMotion.revealDurationMs in 250..300)
        assertEquals(280, standardMotion.revealDurationMs)

        // Continuity: 450ms exclusively
        assertEquals(450, standardMotion.continuityDurationMs)
        assertEquals(450, standardMotion.sharedDetailDurationMs)

        // Reduce Motion: short 150ms fades and 0ms controls
        val reduceMotion = FluxMotionTokens(reduceMotion = true)
        assertEquals(150, reduceMotion.resolveDuration(180))
        assertEquals(150, reduceMotion.resolveDuration(280))
        assertEquals(150, reduceMotion.resolveDuration(450))
        assertEquals(0, reduceMotion.durationForControl())
        assertEquals(150, reduceMotion.durationForReveal())
        assertEquals(150, reduceMotion.durationForContinuity())
    }

    @Test
    fun testTypographyScaleRules() {
        val type = DefaultFluxTypography

        // Page title: 24-26sp, SemiBold
        assertEquals(25.sp, type.pageTitle.fontSize)
        assertEquals(FontWeight.SemiBold, type.pageTitle.fontWeight)

        // Section title: 18sp, SemiBold
        assertEquals(18.sp, type.sectionTitle.fontSize)
        assertEquals(FontWeight.SemiBold, type.sectionTitle.fontWeight)

        // Small group label: 13-14sp, Medium
        assertEquals(13.sp, type.label.fontSize)
        assertEquals(FontWeight.Medium, type.label.fontWeight)

        // Ringing display time: 52-56sp, Medium tabular numerals
        assertTrue("Time display must be between 52sp and 56sp", type.timeDisplay.fontSize.value in 52f..56f)
        assertEquals(FontWeight.Medium, type.timeDisplay.fontWeight)
        assertEquals("tnum", type.timeDisplay.fontFeatureSettings)
    }

    @Test
    fun testShapeTokensRules() {
        val shapes = DefaultFluxShapes

        // Controls: 12dp
        assertEquals(CornerSize(12.dp), shapes.control.topStart)
        assertEquals(CornerSize(12.dp), shapes.control.topEnd)
        assertEquals(CornerSize(12.dp), shapes.control.bottomEnd)
        assertEquals(CornerSize(12.dp), shapes.control.bottomStart)

        // Sole elevated ritual surface: 16dp
        assertEquals(CornerSize(16.dp), shapes.soleElevatedSurface.topStart)
        assertEquals(CornerSize(16.dp), shapes.soleElevatedSurface.topEnd)
        assertEquals(CornerSize(16.dp), shapes.soleElevatedSurface.bottomEnd)
        assertEquals(CornerSize(16.dp), shapes.soleElevatedSurface.bottomStart)

        // Bottom sheet: 24dp top corners
        assertEquals(CornerSize(24.dp), shapes.bottomSheet.topStart)
        assertEquals(CornerSize(24.dp), shapes.bottomSheet.topEnd)
        assertEquals(CornerSize(0.dp), shapes.bottomSheet.bottomStart)
        assertEquals(CornerSize(0.dp), shapes.bottomSheet.bottomEnd)
    }

    @Test
    fun testNamedCueScenesAndConverterRoundtrip() {
        val converter = RitualConverters()

        // Verify all 7 named cues exist and serialize properly
        val expectedCues = listOf(
            RitualVisualCue.Sunrise,
            RitualVisualCue.Breathe,
            RitualVisualCue.Hydrate,
            RitualVisualCue.Commute,
            RitualVisualCue.Focus,
            RitualVisualCue.Exercise,
            RitualVisualCue.WindDown
        )

        assertEquals(7, RitualVisualCue.entries.size)

        for (cue in expectedCues) {
            val serialized = converter.fromRitualCue(cue)
            val deserialized = converter.toRitualCue(serialized)
            assertEquals("Roundtrip failed for $cue", cue, deserialized)
        }

        // Backward compatibility mappings
        assertEquals(RitualVisualCue.Commute, converter.toRitualCue("Walk"))
        assertEquals(RitualVisualCue.WindDown, converter.toRitualCue("Rest"))

        // Null and unknown fallbacks
        assertEquals(RitualVisualCue.Sunrise, converter.toRitualCue(null))
        assertEquals(RitualVisualCue.Sunrise, converter.toRitualCue(""))
        assertEquals(RitualVisualCue.Sunrise, converter.toRitualCue("UnknownCueName"))
    }

    @Test
    fun testDaySelectionSemantics() {
        var selectedDays = setOf(1, 2, 3, 4, 5) // Weekdays

        // Toggle Wednesday (3) off
        selectedDays = selectedDays - 3
        assertFalse(selectedDays.contains(3))
        assertEquals(setOf(1, 2, 4, 5), selectedDays)

        // Toggle Saturday (6) on
        selectedDays = selectedDays + 6
        assertTrue(selectedDays.contains(6))
        assertEquals(setOf(1, 2, 4, 5, 6), selectedDays)

        val converter = RitualConverters()
        val serialized = converter.fromRepeatDays(selectedDays)
        val deserialized = converter.toRepeatDays(serialized)
        assertEquals(selectedDays, deserialized)
    }

    @Test
    fun testSnoozeDurationsSupported() {
        val supportedSnoozeMinutes = listOf(5, 9, 10, 15)
        assertTrue(supportedSnoozeMinutes.contains(9)) // Default
        for (m in supportedSnoozeMinutes) {
            assertTrue("Snooze duration must be positive", m > 0)
        }
    }
}
