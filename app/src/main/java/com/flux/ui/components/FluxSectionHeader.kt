package com.flux.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
fun FluxSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = spacing.gutter,
                end = spacing.gutter,
                top = spacing.xl,
                bottom = spacing.xs
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = typography.sectionTitle,
            color = colors.primaryInk
        )

        if (actionText != null && onActionClick != null) {
            Box(
                modifier = Modifier
                    .clip(FluxTheme.shapes.smallControl)
                    .clickable(
                        role = Role.Button,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true)
                    ) { onActionClick() }
                    .padding(horizontal = spacing.xs, vertical = spacing.xxs)
            ) {
                Text(
                    text = actionText,
                    style = typography.label,
                    color = colors.accent
                )
            }
        }
    }
}
