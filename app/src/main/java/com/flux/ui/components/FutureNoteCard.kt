package com.flux.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.flux.ui.foundation.FluxTheme

/**
 * Flat row representation of the note from future-you.
 * Rendered flat with a divider, preserving the single elevated card rule for NextRitualCard.
 */
@Composable
fun FutureNoteCard(
    noteText: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            role = Role.Button,
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = true)
        ) { onClick() }
    } else Modifier

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .then(clickModifier)
            .padding(horizontal = spacing.gutter, vertical = spacing.md)
    ) {
        Text(
            text = "Note from future-you",
            style = typography.label,
            color = colors.secondaryInk
        )
        Spacer(Modifier.height(spacing.xxs))
        Text(
            text = noteText,
            style = typography.body,
            color = colors.primaryInk
        )
        Spacer(Modifier.height(spacing.sm))
        HorizontalDivider(
            thickness = 1.dp,
            color = colors.divider
        )
    }
}
