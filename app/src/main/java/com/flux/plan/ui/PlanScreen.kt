package com.flux.plan.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flux.plan.data.EventEntity
import com.flux.plan.data.HabitEntity
import com.flux.plan.data.TaskEntity
import com.flux.ui.components.EventPlanRow
import com.flux.ui.components.FluxEmptyState
import com.flux.ui.components.FluxIconButton
import com.flux.ui.components.FluxListRow
import com.flux.ui.components.FluxSectionHeader
import com.flux.ui.components.FluxTopBar
import com.flux.ui.components.HabitRow
import com.flux.ui.components.TaskPlanRow
import com.flux.ui.foundation.FluxTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlanScreen(
    tasks: List<TaskEntity>,
    events: List<EventEntity>,
    habits: List<HabitEntity>,
    todayDateString: String,
    onToggleTask: (id: String, completed: Boolean) -> Unit,
    onToggleHabit: (id: String, completedCurrently: Boolean) -> Unit,
    onAddTask: () -> Unit,
    onAddEvent: () -> Unit,
    onAddHabit: () -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onEditEvent: (EventEntity) -> Unit,
    onEditHabit: (HabitEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

    var showAddItemTypeSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.canvas)
    ) {
        FluxTopBar(
            title = "Plan",
            actionIcon = Icons.Default.Add,
            actionDescription = "Add plan item",
            onActionClick = { showAddItemTypeSheet = true }
        )

        val hasAnyItems = tasks.isNotEmpty() || events.isNotEmpty() || habits.isNotEmpty()

        if (!hasAnyItems) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.xl),
                contentAlignment = Alignment.Center
            ) {
                FluxEmptyState(
                    title = "Your plan is clear",
                    description = "Capture simple tasks, local events with travel buffers, and daily focus habits.",
                    actionText = "Add an item",
                    onActionClick = { showAddItemTypeSheet = true }
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // --- Events Section ---
                if (events.isNotEmpty()) {
                    item {
                        FluxSectionHeader(
                            title = "Events",
                            actionText = "+ Add",
                            onActionClick = onAddEvent
                        )
                    }
                    items(events, key = { it.id }) { event ->
                        val timeStr = SimpleDateFormat("h:mm a", Locale.US).format(Date(event.startTimeMillis))
                        val leaveByStr = event.leaveByTimeMillis?.let {
                            SimpleDateFormat("h:mm a", Locale.US).format(Date(it))
                        }
                        EventPlanRow(
                            title = event.title,
                            timeText = timeStr,
                            leaveByText = leaveByStr,
                            onClick = { onEditEvent(event) }
                        )
                    }
                }

                // --- Tasks Section ---
                if (tasks.isNotEmpty()) {
                    item {
                        FluxSectionHeader(
                            title = "Tasks",
                            actionText = "+ Add",
                            onActionClick = onAddTask
                        )
                    }
                    items(tasks, key = { it.id }) { task ->
                        val dueStr = task.dueDateMillis?.let {
                            SimpleDateFormat("h:mm a", Locale.US).format(Date(it))
                        }
                        TaskPlanRow(
                            title = task.title,
                            isCompleted = task.isCompleted,
                            dueTime = dueStr,
                            onToggleCompleted = { onToggleTask(task.id, !task.isCompleted) },
                            onClick = { onEditTask(task) }
                        )
                    }
                }

                // --- Habits Section ---
                if (habits.isNotEmpty()) {
                    item {
                        FluxSectionHeader(
                            title = "Habits",
                            actionText = "+ Add",
                            onActionClick = onAddHabit
                        )
                    }
                    items(habits, key = { it.id }) { habit ->
                        val isCompletedToday = habit.lastCompletedDate == todayDateString
                        HabitRow(
                            title = habit.title,
                            isCompletedToday = isCompletedToday,
                            scheduleSummary = if (habit.isPaused) "Paused" else "Daily",
                            onToggleCompleted = { onToggleHabit(habit.id, isCompletedToday) },
                            onClick = { onEditHabit(habit) }
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(spacing.xxl))
                }
            }
        }
    }

    if (showAddItemTypeSheet) {
        AddItemTypeBottomSheet(
            onSelectTask = {
                showAddItemTypeSheet = false
                onAddTask()
            },
            onSelectEvent = {
                showAddItemTypeSheet = false
                onAddEvent()
            },
            onSelectHabit = {
                showAddItemTypeSheet = false
                onAddHabit()
            },
            onDismiss = { showAddItemTypeSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddItemTypeBottomSheet(
    onSelectTask: () -> Unit,
    onSelectEvent: () -> Unit,
    onSelectHabit: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography
    val spacing = FluxTheme.spacing
    val shapes = FluxTheme.shapes

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
                .padding(bottom = spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            Text(
                text = "Add to plan",
                style = typography.sectionTitle,
                color = colors.primaryInk,
                modifier = Modifier.padding(bottom = spacing.sm)
            )

            FluxListRow(
                title = "Task",
                subtitle = "Simple item due today or unscheduled",
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        tint = colors.accent
                    )
                },
                onClick = onSelectTask
            )

            FluxListRow(
                title = "Event",
                subtitle = "Local calendar event with leave-by buffer",
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = colors.accent
                    )
                },
                onClick = onSelectEvent
            )

            FluxListRow(
                title = "Habit",
                subtitle = "Gentle daily focus habit",
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = null,
                        tint = colors.accent
                    )
                },
                onClick = onSelectHabit
            )
        }
    }
}
