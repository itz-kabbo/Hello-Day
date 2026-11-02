package com.flux.ritual.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flux.ritual.data.RitualEntity
import com.flux.ui.components.FluxEmptyState
import com.flux.ui.components.FluxTopBar
import com.flux.ui.components.RitualCue
import com.flux.ui.foundation.FluxTheme
import java.util.Locale

/**
 * Rituals screen rendered as a calm, flat list with dividers.
 * No elevated cards or card grids — only Today features a single elevated card.
 */
@Composable
fun RitualsScreen(
    rituals: List<RitualEntity>,
    canScheduleExact: Boolean,
    onAddRitual: () -> Unit,
    onEditRitual: (RitualEntity) -> Unit,
    onToggleEnabled: (id: String, enabled: Boolean) -> Unit,
    onRequestExactPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.canvas)
    ) {
        FluxTopBar(
            title = "Rituals",
            actionIcon = Icons.Default.Add,
            actionDescription = "Create new ritual",
            onActionClick = onAddRitual
        )

        if (!canScheduleExact) {
            // Quiet warning banner explaining exact timing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gutter, vertical = spacing.xs)
                    .clip(shapes.control)
                    .background(colors.warning.copy(alpha = 0.12f))
                    .clickable { onRequestExactPermission() }
                    .padding(spacing.md)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Permission warning",
                        tint = colors.warning,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(spacing.sm))
                    Text(
                        text = "Exact alarm permission missing. Timing may drift.",
                        style = typography.caption.copy(fontWeight = FontWeight.Medium),
                        color = colors.warning
                    )
                }
            }
        }

        if (rituals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.xl),
                contentAlignment = Alignment.Center
            ) {
                FluxEmptyState(
                    title = "No rituals scheduled",
                    description = "Create your first ritual alarm to begin your day with a calm, gentle sequence.",
                    actionText = "Create ritual",
                    onActionClick = onAddRitual
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(rituals, key = { it.id }) { ritual ->
                    RitualCardRow(
                        ritual = ritual,
                        onToggleEnabled = { onToggleEnabled(ritual.id, it) },
                        onClick = { onEditRitual(ritual) }
                    )
                }

                item {
                    Spacer(Modifier.height(spacing.xxl))
                }
            }
        }
    }
}

/**
 * Flat row representation of a ritual item with divider.
 * Clean, serene row with no elevated card or grey icon box.
 */
@Composable
fun RitualCardRow(
    ritual: RitualEntity,
    onToggleEnabled: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing

    val timeString = formatTime(ritual.hour, ritual.minute)
    val repeatSummary = formatRepeatDays(ritual.repeatDays)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = spacing.minInteractiveTarget)
                .clickable(
                    role = Role.Button,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true)
                ) { onClick() }
                .padding(horizontal = spacing.gutter, vertical = spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Named original cue scene (no grey square container)
            RitualCue(
                cue = ritual.cue,
                size = 36.dp,
                useMorningWarmth = ritual.isEnabled
            )

            Spacer(Modifier.width(spacing.md))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    Text(
                        text = timeString,
                        style = typography.tabularTime.copy(fontSize = typography.sectionTitle.fontSize),
                        color = if (ritual.isEnabled) colors.primaryInk else colors.secondaryInk
                    )
                    Text(
                        text = "•",
                        style = typography.caption,
                        color = colors.secondaryInk
                    )
                    Text(
                        text = ritual.title,
                        style = typography.itemTitle,
                        color = if (ritual.isEnabled) colors.primaryInk else colors.secondaryInk
                    )
                }

                Spacer(Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    Text(
                        text = repeatSummary,
                        style = typography.caption,
                        color = colors.secondaryInk
                    )

                    if (ritual.steps.isNotEmpty()) {
                        Text(
                            text = "•",
                            style = typography.caption,
                            color = colors.secondaryInk
                        )
                        Text(
                            text = "${ritual.steps.size} step${if (ritual.steps.size > 1) "s" else ""}",
                            style = typography.caption.copy(fontWeight = FontWeight.Medium),
                            color = if (ritual.isEnabled) colors.accent else colors.secondaryInk
                        )
                    }
                }

                if (ritual.steps.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = ritual.steps.joinToString(" → "),
                        style = typography.caption,
                        color = colors.secondaryInk,
                        maxLines = 1
                    )
                }
            }

            Spacer(Modifier.width(spacing.sm))

            Switch(
                checked = ritual.isEnabled,
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

        HorizontalDivider(
            modifier = Modifier.padding(start = spacing.gutter),
            thickness = 1.dp,
            color = colors.divider
        )
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    val isPm = hour >= 12
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format(Locale.US, "%d:%02d %s", displayHour, minute, if (isPm) "PM" else "AM")
}

private fun formatRepeatDays(days: Set<Int>): String {
    if (days.isEmpty()) return "Once"
    if (days.size == 7) return "Every day"
    if (days == setOf(1, 2, 3, 4, 5)) return "Weekdays"
    if (days == setOf(6, 7)) return "Weekends"

    val dayNames = listOf(
        1 to "Mon",
        2 to "Tue",
        3 to "Wed",
        4 to "Thu",
        5 to "Fri",
        6 to "Sat",
        7 to "Sun"
    )

    return days.sorted()
        .mapNotNull { dayNum -> dayNames.firstOrNull { it.first == dayNum }?.second }
        .joinToString(", ")
}
