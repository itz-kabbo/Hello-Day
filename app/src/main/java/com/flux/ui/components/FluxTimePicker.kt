package com.flux.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.flux.ui.foundation.FluxTheme
import java.util.Locale

@Composable
fun FluxTimePicker(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
    is24Hour: Boolean = false
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    var hour by remember(initialHour) { mutableIntStateOf(initialHour) }
    var minute by remember(initialMinute) { mutableIntStateOf(initialMinute) }
    var isPm by remember(initialHour) { mutableStateOf(initialHour >= 12) }

    val displayHour = when {
        is24Hour -> hour
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapes.grouped)
            .background(colors.surface)
            .padding(spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Hour control
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FluxTimeStepper(
                    value = String.format(Locale.US, "%02d", displayHour),
                    onIncrement = {
                        hour = (hour + 1) % 24
                        isPm = hour >= 12
                        onTimeSelected(hour, minute)
                    },
                    onDecrement = {
                        hour = if (hour - 1 < 0) 23 else hour - 1
                        isPm = hour >= 12
                        onTimeSelected(hour, minute)
                    }
                )
            }

            Text(
                text = ":",
                style = typography.timeDisplay,
                color = colors.primaryInk,
                modifier = Modifier.padding(horizontal = spacing.xs)
            )

            // Minute control
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FluxTimeStepper(
                    value = String.format(Locale.US, "%02d", minute),
                    onIncrement = {
                        minute = (minute + 5) % 60
                        onTimeSelected(hour, minute)
                    },
                    onDecrement = {
                        minute = if (minute - 5 < 0) 55 else minute - 5
                        onTimeSelected(hour, minute)
                    }
                )
            }

            if (!is24Hour) {
                Spacer(Modifier.width(spacing.md))
                // AM / PM Selector
                Column {
                    FluxAmPmToggle(
                        isPm = isPm,
                        onToggle = { pm ->
                            isPm = pm
                            hour = if (pm) {
                                if (hour < 12) hour + 12 else hour
                            } else {
                                if (hour >= 12) hour - 12 else hour
                            }
                            onTimeSelected(hour, minute)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FluxTimeStepper(
    value: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .clip(shapes.smallControl)
                .clickable(
                    role = Role.Button,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true)
                ) { onIncrement() }
                .padding(horizontal = spacing.md, vertical = spacing.xxs)
        ) {
            Text(
                text = "▲",
                style = typography.caption,
                color = colors.accent
            )
        }

        Box(
            modifier = Modifier
                .clip(shapes.control)
                .background(colors.surfaceSubtle)
                .padding(horizontal = spacing.md, vertical = spacing.xs),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                style = typography.timeDisplay,
                color = colors.primaryInk
            )
        }

        Box(
            modifier = Modifier
                .clip(shapes.smallControl)
                .clickable(
                    role = Role.Button,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true)
                ) { onDecrement() }
                .padding(horizontal = spacing.md, vertical = spacing.xxs)
        ) {
            Text(
                text = "▼",
                style = typography.caption,
                color = colors.accent
            )
        }
    }
}

@Composable
private fun FluxAmPmToggle(
    isPm: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    Column(
        modifier = Modifier
            .clip(shapes.control)
            .background(colors.surfaceSubtle)
            .padding(2.dp)
    ) {
        val amSelected = !isPm
        Box(
            modifier = Modifier
                .clip(shapes.smallControl)
                .background(if (amSelected) colors.surface else colors.surfaceSubtle)
                .clickable { onToggle(false) }
                .padding(horizontal = spacing.sm, vertical = spacing.xxs),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AM",
                style = typography.label,
                color = if (amSelected) colors.primaryInk else colors.secondaryInk
            )
        }

        Box(
            modifier = Modifier
                .clip(shapes.smallControl)
                .background(if (isPm) colors.surface else colors.surfaceSubtle)
                .clickable { onToggle(true) }
                .padding(horizontal = spacing.sm, vertical = spacing.xxs),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PM",
                style = typography.label,
                color = if (isPm) colors.primaryInk else colors.secondaryInk
            )
        }
    }
}
