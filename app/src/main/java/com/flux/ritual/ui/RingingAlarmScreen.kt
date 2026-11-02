package com.flux.ritual.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flux.ritual.data.RitualEntity
import com.flux.ui.components.FluxPrimaryButton
import com.flux.ui.components.FluxSecondaryButton
import com.flux.ui.components.RitualCueAnimation
import com.flux.ui.components.RitualVisualCue
import com.flux.ui.foundation.FluxTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Ringing alarm screen as the tranquil visual centerpiece of Flux.
 * Layout: Large 52-56sp tabular time, central settled cue scene on quiet warm field,
 * ritual title, simple unboxed next step, and 12dp Stop/Snooze actions.
 *
 * Designed with a max-width constraint for balanced presentation on both phone and tablet.
 */
@Composable
fun RingingAlarmScreen(
    ritual: RitualEntity?,
    onStop: () -> Unit,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing

    var currentTimeString by remember {
        mutableStateOf(SimpleDateFormat("h:mm a", Locale.US).format(Date()))
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTimeString = SimpleDateFormat("h:mm a", Locale.US).format(Date())
            delay(1000L)
        }
    }

    val cue = ritual?.cue ?: RitualVisualCue.Sunrise
    val title = ritual?.title ?: "Morning ritual"
    val snoozeMinutes = ritual?.snoozeMinutes ?: 9
    val firstStep = ritual?.steps?.firstOrNull() ?: "Wake up gently"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.canvas)
            .padding(horizontal = spacing.xl, vertical = spacing.xxl),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header / Time Section (large time display reserved for ringing screen)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = spacing.lg)
            ) {
                Text(
                    text = currentTimeString,
                    style = typography.timeDisplay,
                    color = colors.primaryInk,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(spacing.xs))

                Text(
                    text = title,
                    style = typography.itemTitle,
                    color = colors.secondaryInk,
                    textAlign = TextAlign.Center
                )
            }

            // Central Animated Cue Scene (settles gently after arrival; no permanent loop)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.lg)
            ) {
                RitualCueAnimation(
                    cue = cue,
                    size = 160.dp
                )

                // Simple unboxed next step callout (flat typography, no card box)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = spacing.md)
                ) {
                    Text(
                        text = "Next: $firstStep",
                        style = typography.itemTitle,
                        color = colors.primaryInk,
                        textAlign = TextAlign.Center
                    )

                    if ((ritual?.steps?.size ?: 0) > 1) {
                        Spacer(Modifier.height(spacing.xxs))
                        val remainingCount = (ritual?.steps?.size ?: 0) - 1
                        Text(
                            text = "+ $remainingCount more step${if (remainingCount > 1) "s" else ""}",
                            style = typography.caption,
                            color = colors.secondaryInk,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Action Buttons (12dp controls, distinct hierarchy)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                FluxPrimaryButton(
                    text = "Stop",
                    onClick = onStop,
                    modifier = Modifier.fillMaxWidth()
                )

                FluxSecondaryButton(
                    text = "Snooze ($snoozeMinutes min)",
                    onClick = onSnooze,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
