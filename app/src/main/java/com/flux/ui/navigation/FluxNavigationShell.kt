package com.flux.ui.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flux.plan.data.EventEntity
import com.flux.plan.data.HabitEntity
import com.flux.plan.data.TaskEntity
import com.flux.plan.ui.CreateEditEventSheet
import com.flux.plan.ui.CreateEditHabitSheet
import com.flux.plan.ui.CreateEditTaskSheet
import com.flux.plan.ui.EditFutureNoteSheet
import com.flux.plan.ui.PlanScreen
import com.flux.plan.ui.PlanViewModel
import com.flux.ritual.data.RitualEntity
import com.flux.ritual.ui.EditRitualScreen
import com.flux.ritual.ui.RitualsScreen
import com.flux.ritual.ui.RitualsViewModel
import com.flux.ui.components.EventPlanRow
import com.flux.ui.components.FluxBottomNavigation
import com.flux.ui.components.FluxNavigationDestination
import com.flux.ui.components.FluxPermissionEducationSheet
import com.flux.ui.components.FluxScaffold
import com.flux.ui.components.FluxSectionHeader
import com.flux.ui.components.FluxTopBar
import com.flux.ui.components.FutureNoteCard
import com.flux.ui.components.HabitRow
import com.flux.ui.components.NextRitualCard
import com.flux.ui.components.RitualVisualCue
import com.flux.ui.components.TaskPlanRow
import com.flux.ui.foundation.FluxTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.flux.settings.FluxAppSettings
import com.flux.settings.SettingsScreen
import com.flux.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FluxNavigationShell(
    modifier: Modifier = Modifier,
    ritualsViewModel: RitualsViewModel? = null,
    planViewModel: PlanViewModel? = null,
    settingsViewModel: SettingsViewModel? = null,
    onOpenSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedDestination by remember { mutableStateOf(FluxNavigationDestination.Today) }

    // Settings screen state
    var showSettings by remember { mutableStateOf(false) }

    // Ritual sheet states
    var editingRitual by remember { mutableStateOf<RitualEntity?>(null) }
    var isCreatingRitual by remember { mutableStateOf(false) }
    var showPermissionEducation by remember { mutableStateOf(false) }

    // Plan sheet states
    var showFutureNoteSheet by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var isCreatingTask by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<EventEntity?>(null) }
    var isCreatingEvent by remember { mutableStateOf(false) }
    var editingHabit by remember { mutableStateOf<HabitEntity?>(null) }
    var isCreatingHabit by remember { mutableStateOf(false) }

    // Settings state
    val appSettings by settingsViewModel?.settings?.collectAsState() ?: remember {
        mutableStateOf(FluxAppSettings())
    }
    val hasExactAlarm = settingsViewModel?.hasExactAlarmPermission() ?: true
    val hasNotification = settingsViewModel?.hasNotificationPermission() ?: true

    // If currently viewing settings, show the settings screen
    if (showSettings) {
        SettingsScreen(
            settings = appSettings,
            hasExactAlarm = hasExactAlarm,
            hasNotification = hasNotification,
            onSetThemeMode = { settingsViewModel?.setThemeMode(it) },
            onSetReduceMotion = { settingsViewModel?.setReduceMotion(it) },
            onSetDefaultSnooze = { settingsViewModel?.setDefaultSnoozeMinutes(it) },
            onSetDefaultVibrate = { settingsViewModel?.setDefaultVibrate(it) },
            onNavigateBack = { showSettings = false }
        )
        return
    }

    // Rituals state
    val rituals by ritualsViewModel?.rituals?.collectAsState() ?: remember {
        mutableStateOf(
            listOf(
                RitualEntity(
                    id = "sample-1",
                    title = "Weekday start",
                    hour = 6,
                    minute = 30,
                    repeatDays = setOf(1, 2, 3, 4, 5),
                    cue = RitualVisualCue.Sunrise,
                    steps = listOf("Wake up", "Make breakfast", "Get ready")
                )
            )
        )
    }

    // Plan state
    val tasks by planViewModel?.tasks?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val tasksDueToday by planViewModel?.tasksDueToday?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val events by planViewModel?.events?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val eventsForToday by planViewModel?.eventsForToday?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val habits by planViewModel?.habits?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val focusedHabits by planViewModel?.focusedHabits?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val surfacedNote by planViewModel?.surfacedNote?.collectAsState() ?: remember { mutableStateOf(null) }
    val todayDateString = planViewModel?.todayDateString ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    val canScheduleExact = ritualsViewModel?.canScheduleExact() ?: true

    // If currently creating or editing a ritual, show the full edit screen
    if (isCreatingRitual || editingRitual != null) {
        EditRitualScreen(
            initialRitual = editingRitual,
            onSave = { ritualToSave ->
                ritualsViewModel?.saveRitual(ritualToSave)
                isCreatingRitual = false
                editingRitual = null
            },
            onDelete = { ritualIdToDelete ->
                ritualsViewModel?.deleteRitual(ritualIdToDelete)
                isCreatingRitual = false
                editingRitual = null
            },
            onNavigateBack = {
                isCreatingRitual = false
                editingRitual = null
            }
        )
        return
    }

    FluxScaffold(
        modifier = modifier,
        bottomBar = {
            FluxBottomNavigation(
                selectedDestination = selectedDestination,
                onDestinationSelected = { selectedDestination = it }
            )
        }
    ) { innerPadding ->
        Crossfade(
            targetState = selectedDestination,
            animationSpec = tween(FluxTheme.motion.resolveDuration(FluxTheme.motion.layoutDurationMs)),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "DestinationCrossfade"
        ) { destination ->
            when (destination) {
                FluxNavigationDestination.Today -> {
                    TodayDestination(
                        rituals = rituals,
                        eventsForToday = eventsForToday,
                        tasksDueToday = tasksDueToday,
                        focusedHabits = focusedHabits,
                        surfacedNote = surfacedNote?.content,
                        todayDateString = todayDateString,
                        onOpenSettings = { showSettings = true },
                        onRitualClick = { editingRitual = it },
                        onToggleRitual = { id, enabled -> ritualsViewModel?.toggleEnabled(id, enabled) },
                        onCreateRitual = { isCreatingRitual = true },
                        onFutureNoteClick = { showFutureNoteSheet = true },
                        onToggleTask = { id, completed -> planViewModel?.toggleTaskCompletion(id, completed) },
                        onToggleHabit = { id, current -> planViewModel?.toggleHabitCompletionToday(id, current) },
                        onEventClick = { editingEvent = it },
                        onTaskClick = { editingTask = it },
                        onHabitClick = { editingHabit = it },
                        onAddEvent = { isCreatingEvent = true },
                        onAddTask = { isCreatingTask = true },
                        onAddHabit = { isCreatingHabit = true }
                    )
                }
                FluxNavigationDestination.Rituals -> {
                    RitualsScreen(
                        rituals = rituals,
                        canScheduleExact = canScheduleExact,
                        onAddRitual = { isCreatingRitual = true },
                        onEditRitual = { editingRitual = it },
                        onToggleEnabled = { id, enabled -> ritualsViewModel?.toggleEnabled(id, enabled) },
                        onRequestExactPermission = { showPermissionEducation = true }
                    )
                }
                FluxNavigationDestination.Plan -> {
                    PlanScreen(
                        tasks = tasks,
                        events = events,
                        habits = habits,
                        todayDateString = todayDateString,
                        onToggleTask = { id, comp -> planViewModel?.toggleTaskCompletion(id, comp) },
                        onToggleHabit = { id, curr -> planViewModel?.toggleHabitCompletionToday(id, curr) },
                        onAddTask = { isCreatingTask = true },
                        onAddEvent = { isCreatingEvent = true },
                        onAddHabit = { isCreatingHabit = true },
                        onEditTask = { editingTask = it },
                        onEditEvent = { editingEvent = it },
                        onEditHabit = { editingHabit = it }
                    )
                }
            }
        }
    }

    // Modal Sheets
    if (showPermissionEducation) {
        FluxPermissionEducationSheet(
            title = "Exact wake-up timing",
            explanation = "To ensure your ritual alarm rings precisely when scheduled without Android delaying it in battery saver mode, Flux requires permission to schedule exact alarms.",
            onConfirm = {
                showPermissionEducation = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    try { context.startActivity(intent) } catch (_: Exception) {}
                }
            },
            onDismiss = { showPermissionEducation = false }
        )
    }

    if (showFutureNoteSheet) {
        EditFutureNoteSheet(
            initialNote = surfacedNote?.content ?: "",
            onSave = { noteContent ->
                planViewModel?.setFutureNote(noteContent)
                showFutureNoteSheet = false
            },
            onDismiss = { showFutureNoteSheet = false }
        )
    }

    if (isCreatingTask || editingTask != null) {
        CreateEditTaskSheet(
            initialTask = editingTask,
            onSave = { taskToSave ->
                planViewModel?.saveTask(taskToSave)
                isCreatingTask = false
                editingTask = null
            },
            onDelete = { taskId ->
                planViewModel?.deleteTask(taskId)
                isCreatingTask = false
                editingTask = null
            },
            onDismiss = {
                isCreatingTask = false
                editingTask = null
            }
        )
    }

    if (isCreatingEvent || editingEvent != null) {
        CreateEditEventSheet(
            initialEvent = editingEvent,
            onSave = { eventToSave ->
                planViewModel?.saveEvent(eventToSave)
                isCreatingEvent = false
                editingEvent = null
            },
            onDelete = { eventId ->
                planViewModel?.deleteEvent(eventId)
                isCreatingEvent = false
                editingEvent = null
            },
            onDismiss = {
                isCreatingEvent = false
                editingEvent = null
            }
        )
    }

    if (isCreatingHabit || editingHabit != null) {
        CreateEditHabitSheet(
            initialHabit = editingHabit,
            onSave = { habitToSave ->
                planViewModel?.saveHabit(habitToSave)
                isCreatingHabit = false
                editingHabit = null
            },
            onDelete = { habitId ->
                planViewModel?.deleteHabit(habitId)
                isCreatingHabit = false
                editingHabit = null
            },
            onDismiss = {
                isCreatingHabit = false
                editingHabit = null
            }
        )
    }
}

@Composable
private fun TodayDestination(
    rituals: List<RitualEntity>,
    eventsForToday: List<EventEntity>,
    tasksDueToday: List<TaskEntity>,
    focusedHabits: List<HabitEntity>,
    surfacedNote: String?,
    todayDateString: String,
    onOpenSettings: () -> Unit,
    onRitualClick: (RitualEntity) -> Unit,
    onToggleRitual: (id: String, enabled: Boolean) -> Unit,
    onCreateRitual: () -> Unit,
    onFutureNoteClick: () -> Unit,
    onToggleTask: (id: String, completed: Boolean) -> Unit,
    onToggleHabit: (id: String, completedCurrently: Boolean) -> Unit,
    onEventClick: (EventEntity) -> Unit,
    onTaskClick: (TaskEntity) -> Unit,
    onHabitClick: (HabitEntity) -> Unit,
    onAddEvent: () -> Unit,
    onAddTask: () -> Unit,
    onAddHabit: () -> Unit
) {
    val spacing = FluxTheme.spacing
    val colors = FluxTheme.colors
    val typography = FluxTheme.typography

    val nextEnabledRitual = rituals.firstOrNull { it.isEnabled } ?: rituals.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvas)
    ) {
        FluxTopBar(
            title = "Today",
            actionIcon = Icons.Outlined.Settings,
            actionDescription = "Settings",
            onActionClick = onOpenSettings
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Anchor 1: Next Ritual Alarm
            item {
                Box(
                    modifier = Modifier.padding(horizontal = spacing.gutter, vertical = spacing.xs)
                ) {
                    if (nextEnabledRitual != null) {
                        NextRitualCard(
                            title = nextEnabledRitual.title,
                            timeText = formatDisplayTime(nextEnabledRitual.hour, nextEnabledRitual.minute),
                            repeatSummary = formatRepeatSummary(nextEnabledRitual.repeatDays),
                            cue = nextEnabledRitual.cue,
                            steps = nextEnabledRitual.steps,
                            isEnabled = nextEnabledRitual.isEnabled,
                            onToggleEnabled = { enabled ->
                                onToggleRitual(nextEnabledRitual.id, enabled)
                            },
                            onClick = { onRitualClick(nextEnabledRitual) }
                        )
                    } else {
                        NextRitualCard(
                            title = "No ritual active",
                            timeText = "--:--",
                            repeatSummary = "Tap to create morning ritual",
                            cue = RitualVisualCue.Sunrise,
                            steps = emptyList(),
                            isEnabled = false,
                            onToggleEnabled = { onCreateRitual() },
                            onClick = onCreateRitual
                        )
                    }
                }
            }

            // Anchor 2: Note from future-you (flat row with divider)
            item {
                FutureNoteCard(
                    noteText = surfacedNote ?: "Write a calm note from future-you to start your day.",
                    onClick = onFutureNoteClick
                )
            }

            // --- Events Section ---
            item {
                FluxSectionHeader(
                    title = "Today's events",
                    actionText = "+ Add",
                    onActionClick = onAddEvent
                )
            }
            if (eventsForToday.isEmpty()) {
                item {
                    Text(
                        text = "No events scheduled for today.",
                        style = typography.caption,
                        color = colors.secondaryInk,
                        modifier = Modifier.padding(horizontal = spacing.gutter, vertical = spacing.xs)
                    )
                }
            } else {
                items(eventsForToday, key = { it.id }) { event ->
                    val timeStr = SimpleDateFormat("h:mm a", Locale.US).format(Date(event.startTimeMillis))
                    val leaveByStr = event.leaveByTimeMillis?.let {
                        SimpleDateFormat("h:mm a", Locale.US).format(Date(it))
                    }
                    EventPlanRow(
                        title = event.title,
                        timeText = timeStr,
                        leaveByText = leaveByStr,
                        onClick = { onEventClick(event) }
                    )
                }
            }

            // --- Tasks Section ---
            item {
                FluxSectionHeader(
                    title = "Tasks due today",
                    actionText = "+ Add",
                    onActionClick = onAddTask
                )
            }
            if (tasksDueToday.isEmpty()) {
                item {
                    Text(
                        text = "No tasks due today.",
                        style = typography.caption,
                        color = colors.secondaryInk,
                        modifier = Modifier.padding(horizontal = spacing.gutter, vertical = spacing.xs)
                    )
                }
            } else {
                items(tasksDueToday, key = { it.id }) { task ->
                    val dueStr = task.dueDateMillis?.let {
                        SimpleDateFormat("h:mm a", Locale.US).format(Date(it))
                    }
                    TaskPlanRow(
                        title = task.title,
                        isCompleted = task.isCompleted,
                        dueTime = dueStr,
                        onToggleCompleted = { onToggleTask(task.id, !task.isCompleted) },
                        onClick = { onTaskClick(task) }
                    )
                }
            }

            // --- Habits Section (up to 3) ---
            item {
                FluxSectionHeader(
                    title = "Habits",
                    actionText = "+ Add",
                    onActionClick = onAddHabit
                )
            }
            if (focusedHabits.isEmpty()) {
                item {
                    Text(
                        text = "No focus habits selected.",
                        style = typography.caption,
                        color = colors.secondaryInk,
                        modifier = Modifier.padding(horizontal = spacing.gutter, vertical = spacing.xs)
                    )
                }
            } else {
                items(focusedHabits, key = { it.id }) { habit ->
                    val isCompletedToday = habit.lastCompletedDate == todayDateString
                    HabitRow(
                        title = habit.title,
                        isCompletedToday = isCompletedToday,
                        scheduleSummary = "Daily",
                        onToggleCompleted = { onToggleHabit(habit.id, isCompletedToday) },
                        onClick = { onHabitClick(habit) }
                    )
                }
            }

            item {
                Spacer(Modifier.height(spacing.xxl))
            }
        }
    }
}

private fun formatDisplayTime(hour: Int, minute: Int): String {
    val isPm = hour >= 12
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format(Locale.US, "%d:%02d %s", displayHour, minute, if (isPm) "PM" else "AM")
}

private fun formatRepeatSummary(days: Set<Int>): String {
    if (days.isEmpty()) return "Once"
    if (days.size == 7) return "Every day"
    if (days == setOf(1, 2, 3, 4, 5)) return "Mon, Tue, Wed, Thu, Fri"
    if (days == setOf(6, 7)) return "Sat, Sun"

    val dayNames = listOf(
        1 to "Mon",
        2 to "Tue",
        3 to "Wed",
        4 to "Thu",
        5 to "Fri",
        6 to "Sat",
        7 to "Sun"
    )

    return days.sorted()
        .mapNotNull { dayNum -> dayNames.firstOrNull { it.first == dayNum }?.second }
        .joinToString(", ")
}
