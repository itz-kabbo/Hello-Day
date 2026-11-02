package com.flux.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flux.ui.components.FluxListRow
import com.flux.ui.components.FluxSectionHeader
import com.flux.ui.components.FluxToggleRow
import com.flux.ui.components.FluxTopBar
import com.flux.ui.foundation.FluxTheme

@Composable
fun SettingsScreen(
    settings: FluxAppSettings,
    hasExactAlarm: Boolean,
    hasNotification: Boolean,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetReduceMotion: (Boolean) -> Unit,
    onSetDefaultSnooze: (Int) -> Unit,
    onSetDefaultVibrate: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.canvas)
    ) {
        FluxTopBar(
            title = "Settings",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            navigationDescription = "Back",
            onNavigationClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = spacing.xxl)
        ) {
            // --- Appearance ---
            FluxSectionHeader(title = "Appearance")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gutter),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                ThemeMode.entries.forEach { mode ->
                    val isSelected = settings.themeMode == mode
                    val label = when (mode) {
                        ThemeMode.SYSTEM -> "System"
                        ThemeMode.LIGHT -> "Light"
                        ThemeMode.DARK -> "Dark"
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(shapes.control)
                            .background(if (isSelected) colors.accentSoft else colors.surface)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) colors.accent else colors.divider,
                                shape = shapes.control
                            )
                            .clickable { onSetThemeMode(mode) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = typography.label.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (isSelected) colors.accent else colors.secondaryInk
                        )
                    }
                }
            }

            Spacer(Modifier.height(spacing.md))

            FluxToggleRow(
                title = "Reduce motion",
                subtitle = "Disable gentle breathing transitions",
                checked = settings.reduceMotion,
                onCheckedChange = onSetReduceMotion
            )

            // --- Alarm Defaults ---
            Spacer(Modifier.height(spacing.lg))
            FluxSectionHeader(title = "Alarm defaults")

            FluxToggleRow(
                title = "Vibrate on start",
                checked = settings.defaultVibrate,
                onCheckedChange = onSetDefaultVibrate
            )

            Spacer(Modifier.height(spacing.sm))

            Column(modifier = Modifier.padding(horizontal = spacing.gutter)) {
                Text(
                    text = "Default snooze duration",
                    style = typography.itemTitle,
                    color = colors.primaryInk
                )
                Spacer(Modifier.height(spacing.xs))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    listOf(5, 9, 10, 15).forEach { minutes ->
                        val isSelected = settings.defaultSnoozeMinutes == minutes
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(shapes.control)
                                .background(if (isSelected) colors.accentSoft else colors.surface)
                                .border(
                                    1.dp,
                                    if (isSelected) colors.accent else colors.divider,
                                    shapes.control
                                )
                                .clickable { onSetDefaultSnooze(minutes) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$minutes min",
                                style = typography.caption.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                color = if (isSelected) colors.accent else colors.secondaryInk
                            )
                        }
                    }
                }
            }

            // --- Permissions ---
            Spacer(Modifier.height(spacing.lg))
            FluxSectionHeader(title = "System permissions")

            FluxListRow(
                title = "Exact alarm timing",
                subtitle = if (hasExactAlarm) "Granted • Guaranteed precise trigger" else "Not granted • Alarms may drift",
                leadingContent = {
                    Icon(
                        imageVector = if (hasExactAlarm) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (hasExactAlarm) colors.success else colors.warning
                    )
                },
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = Uri.parse("package:${context.packageName}")
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        try { context.startActivity(intent) } catch (_: Exception) {}
                    }
                }
            )

            FluxListRow(
                title = "Notifications",
                subtitle = if (hasNotification) "Granted • Sound & morning alerts active" else "Not granted • Alarms may not show banners",
                leadingContent = {
                    Icon(
                        imageVector = if (hasNotification) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (hasNotification) colors.success else colors.warning
                    )
                },
                onClick = {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    try { context.startActivity(intent) } catch (_: Exception) {}
                }
            )

            // --- Privacy & About ---
            Spacer(Modifier.height(spacing.lg))
            FluxSectionHeader(title = "Privacy & Philosophy")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .padding(horizontal = spacing.gutter, vertical = spacing.lg),
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                Text(
                    text = "Private & Offline-First",
                    style = typography.itemTitle.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.primaryInk
                )
                Text(
                    text = "Flux does not use accounts, cloud servers, or analytics. Everything you create—rituals, notes, events, and tasks—lives exclusively on this device.",
                    style = typography.body,
                    color = colors.secondaryInk
                )
                Spacer(Modifier.height(spacing.xs))
                Text(
                    text = "Flux 1.0 (Quiet Momentum)",
                    style = typography.caption,
                    color = colors.secondaryInk
                )
            }
        }
    }
}
