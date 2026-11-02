package com.flux.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.flux.ui.foundation.FluxTheme

@Composable
fun FluxPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    val colors = FluxTheme.colors
    val shapes = FluxTheme.shapes
    val typography = FluxTheme.typography

    val bgColor = when {
        !enabled -> colors.divider
        else -> colors.accent
    }
    val contentColor = when {
        !enabled -> colors.secondaryInk
        colors.isDark -> colors.primaryInk
        else -> colors.surface
    }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = FluxTheme.spacing.minInteractiveTarget)
            .clip(shapes.control)
            .background(bgColor)
            .clickable(
                enabled = enabled && !isLoading,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = contentColor)
            ) { onClick() }
            .padding(horizontal = FluxTheme.spacing.lg, vertical = FluxTheme.spacing.sm)
            .semantics {
                role = Role.Button
                if (!enabled) disabled()
            },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(FluxTheme.spacing.xs))
                }
                Text(
                    text = text,
                    style = typography.label,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun FluxSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    isDestructive: Boolean = false
) {
    val colors = FluxTheme.colors
    val shapes = FluxTheme.shapes
    val typography = FluxTheme.typography

    val bgColor = when {
        !enabled -> colors.divider.copy(alpha = 0.5f)
        isDestructive -> colors.destructive.copy(alpha = 0.12f)
        else -> colors.surfaceSubtle
    }
    val contentColor = when {
        !enabled -> colors.secondaryInk
        isDestructive -> colors.destructive
        else -> colors.primaryInk
    }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = FluxTheme.spacing.minInteractiveTarget)
            .clip(shapes.control)
            .background(bgColor)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = contentColor)
            ) { onClick() }
            .padding(horizontal = FluxTheme.spacing.lg, vertical = FluxTheme.spacing.sm)
            .semantics {
                role = Role.Button
                if (!enabled) disabled()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(FluxTheme.spacing.xs))
            }
            Text(
                text = text,
                style = typography.label,
                color = contentColor
            )
        }
    }
}

@Composable
fun FluxIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = FluxTheme.colors.primaryInk
) {
    val effectiveTint = if (enabled) tint else FluxTheme.colors.secondaryInk

    Box(
        modifier = modifier
            .size(FluxTheme.spacing.minInteractiveTarget)
            .clip(FluxTheme.shapes.smallControl)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true)
            ) { onClick() }
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
                if (!enabled) disabled()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = effectiveTint,
            modifier = Modifier.size(22.dp)
        )
    }
}
