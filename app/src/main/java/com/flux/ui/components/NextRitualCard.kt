package com.flux.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.flux.ui.foundation.FluxTheme

/**
 * The sole elevated card on Today: represents the next scheduled ritual.
 * Uses 16dp rounded corner surface, calm semantic colors, and an original cue scene.
 */
@Composable
fun NextRitualCard(
    title: String,
    timeText: String,
    repeatSummary: String,
    cue: RitualVisualCue,
    steps: List<String>,
    isEnabled: Boolean,
    onToggleEnabled: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapes.soleElevatedSurface)
            .background(colors.surface)
            .border(1.dp, colors.divider, shapes.soleElevatedSurface)
            .clickable(
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true)
            ) { onClick() }
            .padding(spacing.lg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RitualCue(
                cue = cue,
                size = 38.dp,
                useMorningWarmth = isEnabled
            )

            Spacer(Modifier.width(spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = timeText,
                    style = typography.tabularTime.copy(fontSize = typography.sectionTitle.fontSize),
                    color = if (isEnabled) colors.primaryInk else colors.secondaryInk
                )
                Text(
                    text = "$title • $repeatSummary",
                    style = typography.caption,
                    color = colors.secondaryInk
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggleEnabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colors.surface,
                    checkedTrackColor = colors.accent,
                    uncheckedThumbColor = colors.surface,
                    uncheckedTrackColor = colors.divider,
                    uncheckedBorderColor = colors.divider
                )
            )
        }

        if (steps.isNotEmpty() && isEnabled) {
            Spacer(Modifier.height(spacing.sm))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.control)
                    .background(colors.surfaceSubtle)
                    .padding(horizontal = spacing.md, vertical = spacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = steps.joinToString(" → "),
                    style = typography.caption,
                    color = colors.primaryInk
                )
            }
        }
    }
}
