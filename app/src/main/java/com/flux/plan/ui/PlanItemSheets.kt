package com.flux.plan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flux.plan.data.EventEntity
import com.flux.plan.data.HabitEntity
import com.flux.plan.data.TaskEntity
import com.flux.ui.components.FluxPrimaryButton
import com.flux.ui.components.FluxSecondaryButton
import com.flux.ui.components.FluxSectionHeader
import com.flux.ui.components.FluxTimePicker
import com.flux.ui.components.FluxToggleRow
import com.flux.ui.foundation.FluxTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditTaskSheet(
    initialTask: TaskEntity? = null,
    onSave: (TaskEntity) -> Unit,
    onDelete: (String) -> Unit = {},
    onDismiss: () -> Unit
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var hasDueDate by remember { mutableStateOf(initialTask?.dueDateMillis != null) }
    var selectedHour by remember { mutableIntStateOf(17) }
    var selectedMinute by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.surface,
        shape = shapes.bottomSheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.xl)
                .navigationBarsPadding()
                .padding(bottom = spacing.xl),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            Text(
                text = if (initialTask != null) "Edit task" else "New task",
                style = typography.sectionTitle,
                color = colors.primaryInk
            )

            // Title input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.control)
                    .background(colors.surfaceSubtle)
                    .padding(spacing.md)
            ) {
                if (title.isEmpty()) {
                    Text(
                        text = "Task name",
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

            FluxToggleRow(
                title = "Set scheduled due time",
                checked = hasDueDate,
                onCheckedChange = { hasDueDate = it }
            )

            if (hasDueDate) {
                FluxTimePicker(
                    initialHour = selectedHour,
                    initialMinute = selectedMinute,
                    onTimeSelected = { h, m ->
                        selectedHour = h
                        selectedMinute = m
                    }
                )
            }

            FluxPrimaryButton(
                text = if (initialTask != null) "Save changes" else "Add task",
                onClick = {
                    if (title.isNotBlank()) {
                        val dueMillis = if (hasDueDate) {
                            Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, selectedHour)
                                set(Calendar.MINUTE, selectedMinute)
                                set(Calendar.SECOND, 0)
                            }.timeInMillis
                        } else null

                        onSave(
                            initialTask?.copy(title = title.trim(), dueDateMillis = dueMillis)
                                ?: TaskEntity(title = title.trim(), dueDateMillis = dueMillis)
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (initialTask != null) {
                FluxSecondaryButton(
                    text = "Delete task",
                    onClick = {
                        onDelete(initialTask.id)
                        onDismiss()
                    },
                    isDestructive = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditEventSheet(
    initialEvent: EventEntity? = null,
    onSave: (EventEntity) -> Unit,
    onDelete: (String) -> Unit = {},
    onDismiss: () -> Unit
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    var title by remember { mutableStateOf(initialEvent?.title ?: "") }
    var startHour by remember { mutableIntStateOf(10) }
    var startMinute by remember { mutableIntStateOf(0) }
    var bufferMinutes by remember { mutableIntStateOf(initialEvent?.bufferMinutes ?: 30) }

    val bufferOptions = listOf(0, 15, 30, 45, 60)

    val calculatedLeaveByString = remember(startHour, startMinute, bufferMinutes) {
        if (bufferMinutes > 0) {
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, startHour)
                set(Calendar.MINUTE, startMinute)
                add(Calendar.MINUTE, -bufferMinutes)
            }
            SimpleDateFormat("h:mm a", Locale.US).format(cal.time)
        } else null
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.surface,
        shape = shapes.bottomSheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.xl)
                .navigationBarsPadding()
                .padding(bottom = spacing.xl),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            Text(
                text = if (initialEvent != null) "Edit event" else "New event",
                style = typography.sectionTitle,
                color = colors.primaryInk
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.control)
                    .background(colors.surfaceSubtle)
                    .padding(spacing.md)
            ) {
                if (title.isEmpty()) {
                    Text(
                        text = "e.g. Design review",
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

            FluxSectionHeader(title = "Event start time")
            FluxTimePicker(
                initialHour = startHour,
                initialMinute = startMinute,
                onTimeSelected = { h, m ->
                    startHour = h
                    startMinute = m
                }
            )

            FluxSectionHeader(title = "Travel / Preparation buffer")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                bufferOptions.forEach { option ->
                    val isSelected = bufferMinutes == option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(shapes.control)
                            .background(if (isSelected) colors.accentSoft else colors.surfaceSubtle)
                            .border(
                                1.dp,
                                if (isSelected) colors.accent else colors.divider,
                                shapes.control
                            )
                            .clickable { bufferMinutes = option },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (option == 0) "None" else "${option}m",
                            style = typography.caption.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (isSelected) colors.accent else colors.secondaryInk
                        )
                    }
                }
            }

            if (calculatedLeaveByString != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shapes.control)
                        .background(colors.morningWarmth)
                        .padding(spacing.md)
                ) {
                    Text(
                        text = "Leave by $calculatedLeaveByString",
                        style = typography.label.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.primaryInk
                    )
                }
            }

            FluxPrimaryButton(
                text = if (initialEvent != null) "Save event" else "Create event",
                onClick = {
                    if (title.isNotBlank()) {
                        val startCal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, startHour)
                            set(Calendar.MINUTE, startMinute)
                            set(Calendar.SECOND, 0)
                        }
                        val endCal = (startCal.clone() as Calendar).apply {
                            add(Calendar.HOUR_OF_DAY, 1)
                        }

                        onSave(
                            initialEvent?.copy(
                                title = title.trim(),
                                startTimeMillis = startCal.timeInMillis,
                                endTimeMillis = endCal.timeInMillis,
                                bufferMinutes = bufferMinutes
                            ) ?: EventEntity(
                                title = title.trim(),
                                startTimeMillis = startCal.timeInMillis,
                                endTimeMillis = endCal.timeInMillis,
                                bufferMinutes = bufferMinutes
                            )
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (initialEvent != null) {
                FluxSecondaryButton(
                    text = "Delete event",
                    onClick = {
                        onDelete(initialEvent.id)
                        onDismiss()
                    },
                    isDestructive = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditHabitSheet(
    initialHabit: HabitEntity? = null,
    onSave: (HabitEntity) -> Unit,
    onDelete: (String) -> Unit = {},
    onDismiss: () -> Unit
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    var title by remember { mutableStateOf(initialHabit?.title ?: "") }
    var isFocused by remember { mutableStateOf(initialHabit?.isFocusedToday ?: true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.surface,
        shape = shapes.bottomSheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.xl)
                .navigationBarsPadding()
                .padding(bottom = spacing.xl),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            Text(
                text = if (initialHabit != null) "Edit habit" else "New habit",
                style = typography.sectionTitle,
                color = colors.primaryInk
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.control)
                    .background(colors.surfaceSubtle)
                    .padding(spacing.md)
            ) {
                if (title.isEmpty()) {
                    Text(
                        text = "e.g. Drink 500ml water",
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

            FluxToggleRow(
                title = "Focus in morning context (up to 3)",
                checked = isFocused,
                onCheckedChange = { isFocused = it }
            )

            FluxPrimaryButton(
                text = if (initialHabit != null) "Save habit" else "Create habit",
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            initialHabit?.copy(title = title.trim(), isFocusedToday = isFocused)
                                ?: HabitEntity(title = title.trim(), isFocusedToday = isFocused)
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (initialHabit != null) {
                FluxSecondaryButton(
                    text = "Delete habit",
                    onClick = {
                        onDelete(initialHabit.id)
                        onDismiss()
                    },
                    isDestructive = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFutureNoteSheet(
    initialNote: String = "",
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    var content by remember { mutableStateOf(initialNote) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.surface,
        shape = shapes.bottomSheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.xl)
                .navigationBarsPadding()
                .padding(bottom = spacing.xl),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            Text(
                text = "Note from future-you",
                style = typography.sectionTitle,
                color = colors.primaryInk
            )

            Text(
                text = "A gentle, personal sentence to greet you in the morning when you complete your wake-up sequence.",
                style = typography.caption,
                color = colors.secondaryInk
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(shapes.control)
                    .background(colors.surfaceSubtle)
                    .padding(spacing.md)
            ) {
                if (content.isEmpty()) {
                    Text(
                        text = "Write your one-line note here...",
                        style = typography.body,
                        color = colors.secondaryInk
                    )
                }
                BasicTextField(
                    value = content,
                    onValueChange = { content = it },
                    textStyle = typography.body.copy(color = colors.primaryInk),
                    cursorBrush = SolidColor(colors.accent),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            FluxPrimaryButton(
                text = "Save note",
                onClick = {
                    onSave(content.trim())
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
