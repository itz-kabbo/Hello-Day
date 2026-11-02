package com.flux.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.flux.ui.foundation.FluxTheme

@Composable
fun FluxTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    navigationDescription: String = "Back",
    actionIcon: ImageVector? = null,
    onActionClick: (() -> Unit)? = null,
    actionDescription: String = "Settings",
    extraAction: @Composable (() -> Unit)? = null
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.canvas)
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = spacing.gutter),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (navigationIcon != null && onNavigationClick != null) {
                FluxIconButton(
                    icon = navigationIcon,
                    contentDescription = navigationDescription,
                    onClick = onNavigationClick
                )
                Spacer(Modifier.width(spacing.xs))
            }

            Text(
                text = title,
                style = typography.pageTitle,
                color = colors.primaryInk,
                modifier = Modifier.weight(1f)
            )

            if (extraAction != null) {
                extraAction()
            }

            if (actionIcon != null && onActionClick != null) {
                Spacer(Modifier.width(spacing.xs))
                FluxIconButton(
                    icon = actionIcon,
                    contentDescription = actionDescription,
                    onClick = onActionClick
                )
            }
        }
    }
}
