package com.flux.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.flux.ui.foundation.FluxTheme

@Composable
fun RitualStepRow(
    stepNumber: Int,
    stepDescription: String,
    modifier: Modifier = Modifier,
    isCompleted: Boolean = false
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isCompleted) colors.success else colors.surfaceSubtle),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber.toString(),
                style = typography.caption,
                color = if (isCompleted) colors.surface else colors.secondaryInk
            )
        }

        Spacer(Modifier.width(spacing.md))

        Text(
            text = stepDescription,
            style = typography.body,
            color = if (isCompleted) colors.secondaryInk else colors.primaryInk
        )
    }
}
