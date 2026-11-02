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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.flux.ui.foundation.FluxTheme

@Composable
fun TaskPlanRow(
    title: String,
    isCompleted: Boolean,
    onToggleCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    dueTime: String? = null,
    onClick: (() -> Unit)? = null,
    showDivider: Boolean = true
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val motion = FluxTheme.motion

    val checkBgColor by animateColorAsState(
        targetValue = if (isCompleted) colors.success else colors.canvas,
        animationSpec = tween(motion.resolveDuration(motion.pressDurationMs)),
        label = "TaskCheckBg"
    )

    val checkBorderColor by animateColorAsState(
        targetValue = if (isCompleted) colors.success else colors.divider,
        animationSpec = tween(motion.resolveDuration(motion.pressDurationMs)),
        label = "TaskCheckBorder"
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
                    role = Role.Checkbox,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onToggleCompleted() }
                .padding(horizontal = spacing.gutter, vertical = spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(checkBgColor)
                    .border(1.5.dp, checkBorderColor, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = colors.surface,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(Modifier.width(spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = typography.itemTitle,
                    color = if (isCompleted) colors.secondaryInk else colors.primaryInk,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                )
                if (!dueTime.isNullOrBlank()) {
                    Text(
                        text = "Due $dueTime",
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

@Composable
fun EventPlanRow(
    title: String,
    timeText: String,
    modifier: Modifier = Modifier,
    leaveByText: String? = null,
    onClick: (() -> Unit)? = null,
    showDivider: Boolean = true
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
    ) {
        val clickModifier = if (onClick != null) {
            Modifier.clickable(
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
        } else Modifier

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = spacing.minInteractiveTarget)
                .then(clickModifier)
                .padding(horizontal = spacing.gutter, vertical = spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(colors.accent)
            )

            Spacer(Modifier.width(spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = typography.itemTitle,
                    color = colors.primaryInk
                )
                if (!leaveByText.isNullOrBlank()) {
                    Text(
                        text = "Leave by $leaveByText",
                        style = typography.caption,
                        color = colors.warning
                    )
                }
            }

            Spacer(Modifier.width(spacing.sm))

            Text(
                text = timeText,
                style = typography.tabularTime,
                color = colors.secondaryInk
            )
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
