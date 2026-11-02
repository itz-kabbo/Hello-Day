package com.flux.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.flux.ui.foundation.FluxTheme

@Composable
fun HabitRow(
    title: String,
    isCompletedToday: Boolean,
    onToggleCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    scheduleSummary: String? = null,
    showDivider: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val motion = FluxTheme.motion

    val checkBgColor by animateColorAsState(
        targetValue = if (isCompletedToday) colors.success else colors.canvas,
        animationSpec = tween(motion.resolveDuration(motion.pressDurationMs)),
        label = "HabitCheckBg"
    )

    val checkBorderColor by animateColorAsState(
        targetValue = if (isCompletedToday) colors.success else colors.divider,
        animationSpec = tween(motion.resolveDuration(motion.pressDurationMs)),
        label = "HabitCheckBorder"
    )

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
                    role = if (onClick != null) Role.Button else Role.Checkbox,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true)
                ) {
                    if (onClick != null) onClick() else onToggleCompleted()
                }
                .padding(horizontal = spacing.gutter, vertical = spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(
                        role = Role.Checkbox,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, radius = 18.dp)
                    ) { onToggleCompleted() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(checkBgColor)
                        .border(1.5.dp, checkBorderColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompletedToday) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = colors.surface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.width(spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = typography.itemTitle,
                    color = if (isCompletedToday) colors.secondaryInk else colors.primaryInk
                )
                if (!scheduleSummary.isNullOrBlank()) {
                    Text(
                        text = scheduleSummary,
                        style = typography.caption,
                        color = colors.secondaryInk
                    )
                }
            }
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = spacing.gutter),
                thickness = 1.dp,
                color = colors.divider
            )
        }
    }
}
