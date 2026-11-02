package com.flux.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.flux.ui.foundation.FluxTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FluxPermissionEducationSheet(
    title: String,
    explanation: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    confirmText: String = "Enable alarm",
    dismissText: String = "Skip for now"
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        shape = shapes.bottomSheet,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = spacing.sm)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(shapes.smallControl)
                    .background(colors.divider)
            )
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.gutter)
                .navigationBarsPadding()
                .padding(bottom = spacing.xl),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = typography.pageTitle,
                color = colors.primaryInk
            )

            Spacer(Modifier.height(spacing.sm))

            Text(
                text = explanation,
                style = typography.body,
                color = colors.secondaryInk
            )

            Spacer(Modifier.height(spacing.xl))

            FluxPrimaryButton(
                text = confirmText,
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(spacing.xs))

            FluxSecondaryButton(
                text = dismissText,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
