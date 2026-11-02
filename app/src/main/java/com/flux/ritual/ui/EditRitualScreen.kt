package com.flux.ritual.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flux.ritual.data.RitualEntity
import com.flux.ui.components.FluxIconButton
import com.flux.ui.components.FluxPrimaryButton
import com.flux.ui.components.FluxSecondaryButton
import com.flux.ui.components.FluxSectionHeader
import com.flux.ui.components.FluxTimePicker
import com.flux.ui.components.FluxToggleRow
import com.flux.ui.components.FluxTopBar
import com.flux.ui.components.RitualCue
import com.flux.ui.components.RitualVisualCue
import com.flux.ui.foundation.FluxTheme
import java.util.UUID

@Composable
fun EditRitualScreen(
    initialRitual: RitualEntity? = null,
    onSave: (RitualEntity) -> Unit,
    onDelete: (String) -> Unit = {},
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    val isEditing = initialRitual != null

    var title by remember { mutableStateOf(initialRitual?.title ?: "Morning ritual") }
    var hour by remember { mutableIntStateOf(initialRitual?.hour ?: 6) }
    var minute by remember { mutableIntStateOf(initialRitual?.minute ?: 30) }
    var repeatDays by remember { mutableStateOf(initialRitual?.repeatDays ?: setOf(1, 2, 3, 4, 5)) }
    var cue by remember { mutableStateOf(initialRitual?.cue ?: RitualVisualCue.Sunrise) }
    var vibrate by remember { mutableStateOf(initialRitual?.vibrate ?: true) }
    var snoozeMinutes by remember { mutableIntStateOf(initialRitual?.snoozeMinutes ?: 9) }
    var steps by remember {
        mutableStateOf(
            initialRitual?.steps ?: listOf("Wake up", "Make breakfast", "Get ready")
        )
    }

    var newStepInput by remember { mutableStateOf("") }
    var showStepInput by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.canvas)
    ) {
        FluxTopBar(
            title = if (isEditing) "Edit ritual" else "New ritual",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.xl, vertical = spacing.sm)
        ) {
            // Time Picker
            FluxTimePicker(
                initialHour = hour,
                initialMinute = minute,
                onTimeSelected = { h, m ->
                    hour = h
                    minute = m
                }
            )

            Spacer(Modifier.height(spacing.lg))

            // Title Input
            FluxSectionHeader(title = "Title")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.control)
                    .background(colors.surface)
                    .border(1.dp, colors.divider, shapes.control)
                    .padding(horizontal = spacing.md, vertical = spacing.md)
            ) {
                if (title.isEmpty()) {
                    Text(
                        text = "e.g. Weekday start",
                        style = typography.body,
                        color = colors.secondaryInk
                    )
                }
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    textStyle = typography.body.copy(color = colors.primaryInk),
                    cursorBrush = SolidColor(colors.accent),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(spacing.lg))

            // Repeat Days Selector
            FluxSectionHeader(title = "Repeat days")
            RepeatDaysSelector(
                selectedDays = repeatDays,
                onDaysChanged = { repeatDays = it }
            )

            Spacer(Modifier.height(spacing.lg))

            // Visual Cue Selector
            FluxSectionHeader(title = "Visual cue")
            VisualCueSelector(
                selectedCue = cue,
                onCueSelected = { cue = it }
            )

            Spacer(Modifier.height(spacing.lg))

            // Ritual Steps Section
            FluxSectionHeader(title = "Ritual sequence (up to 3)")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.grouped)
                    .background(colors.surface)
                    .padding(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                steps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.xxs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(colors.accentSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = typography.label.copy(fontWeight = FontWeight.SemiBold),
                                color = colors.accent
                            )
                        }

                        Spacer(Modifier.width(spacing.sm))

                        Text(
                            text = step,
                            style = typography.body,
                            color = colors.primaryInk,
                            modifier = Modifier.weight(1f)
                        )

                        FluxIconButton(
                            icon = Icons.Default.Close,
                            contentDescription = "Remove step",
                            onClick = {
                                steps = steps.toMutableList().apply { removeAt(index) }
                            }
                        )
                    }
                }

                if (steps.size < 3) {
                    if (showStepInput) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(shapes.control)
                                    .background(colors.surfaceSubtle)
                                    .padding(horizontal = spacing.sm, vertical = spacing.sm)
                            ) {
                                if (newStepInput.isEmpty()) {
                                    Text(
                                        text = "Next step...",
                                        style = typography.body,
                                        color = colors.secondaryInk
                                    )
                                }
                                BasicTextField(
                                    value = newStepInput,
                                    onValueChange = { newStepInput = it },
                                    textStyle = typography.body.copy(color = colors.primaryInk),
                                    cursorBrush = SolidColor(colors.accent),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(Modifier.width(spacing.xs))

                            FluxIconButton(
                                icon = Icons.Default.Add,
                                contentDescription = "Confirm add step",
                                onClick = {
                                    if (newStepInput.isNotBlank()) {
                                        steps = steps + newStepInput.trim()
                                        newStepInput = ""
                                        showStepInput = false
                                    }
                                }
                            )
                        }
                    } else {
                        FluxSecondaryButton(
                            text = "+ Add next step",
                            onClick = { showStepInput = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.height(spacing.lg))

            // Sound & Vibration Controls
            FluxSectionHeader(title = "Alarm preferences")
            FluxToggleRow(
                title = "Vibrate on start",
                checked = vibrate,
                onCheckedChange = { vibrate = it }
            )

            Spacer(Modifier.height(spacing.md))

            // Snooze Duration Selector
            FluxSectionHeader(title = "Snooze duration")
            SnoozeDurationSelector(
                selectedMinutes = snoozeMinutes,
                onSelected = { snoozeMinutes = it }
            )

            Spacer(Modifier.height(spacing.xl))

            // Save Button
            FluxPrimaryButton(
                text = if (isEditing) "Save changes" else "Create ritual",
                onClick = {
                    val entity = RitualEntity(
                        id = initialRitual?.id ?: UUID.randomUUID().toString(),
                        title = title.ifBlank { "Morning ritual" },
                        hour = hour,
                        minute = minute,
                        repeatDays = repeatDays,
                        cue = cue,
                        vibrate = vibrate,
                        snoozeMinutes = snoozeMinutes,
                        isEnabled = initialRitual?.isEnabled ?: true,
                        steps = steps
                    )
                    onSave(entity)
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (initialRitual != null) {
                Spacer(Modifier.height(spacing.sm))
                FluxSecondaryButton(
                    text = "Delete ritual",
                    onClick = { onDelete(initialRitual.id) },
                    isDestructive = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(spacing.xxl))
        }
    }
}

@Composable
private fun RepeatDaysSelector(
    selectedDays: Set<Int>,
    onDaysChanged: (Set<Int>) -> Unit
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val shapes = FluxTheme.shapes

    val days = listOf(
        1 to "M",
        2 to "T",
        3 to "W",
        4 to "T",
        5 to "F",
        6 to "S",
        7 to "S"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { (isoDay, label) ->
            val isSelected = selectedDays.contains(isoDay)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(shapes.control)
                    .background(if (isSelected) colors.accent else colors.surfaceSubtle)
                    .clickable(
                        role = Role.Checkbox,
                        onClick = {
                            val newSet = if (isSelected) selectedDays - isoDay else selectedDays + isoDay
                            onDaysChanged(newSet)
                        }
                    )
                    .semantics {
                        contentDescription = "Day $label ${if (isSelected) "selected" else "unselected"}"
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = typography.label.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isSelected) colors.surface else colors.secondaryInk
                )
            }
        }
    }
}

@Composable
private fun VisualCueSelector(
    selectedCue: RitualVisualCue,
    onCueSelected: (RitualVisualCue) -> Unit
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        RitualVisualCue.entries.forEach { cueOption ->
            val isSelected = selectedCue == cueOption
            Column(
                modifier = Modifier
                    .width(76.dp)
                    .clip(shapes.control)
                    .background(if (isSelected) colors.accentSoft else colors.surface)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) colors.accent else colors.divider,
                        shape = shapes.control
                    )
                    .clickable { onCueSelected(cueOption) }
                    .padding(vertical = spacing.sm, horizontal = spacing.xs)
                    .semantics {
                        contentDescription = "Cue: ${cueOption.label}"
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RitualCue(
                    cue = cueOption,
                    size = 32.dp,
                    useMorningWarmth = isSelected
                )
                Spacer(Modifier.height(spacing.xs))
                Text(
                    text = cueOption.label,
                    style = typography.caption.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 11.sp
                    ),
                    color = if (isSelected) colors.accent else colors.secondaryInk,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun SnoozeDurationSelector(
    selectedMinutes: Int,
    onSelected: (Int) -> Unit
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    val options = listOf(5, 9, 10, 15)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        options.forEach { minutes ->
            val isSelected = selectedMinutes == minutes
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
                    .clickable { onSelected(minutes) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$minutes min",
                    style = typography.label.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = if (isSelected) colors.accent else colors.secondaryInk
                )
            }
        }
    }
}
