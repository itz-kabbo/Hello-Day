package com.flux.ui.previews

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flux.ui.components.EventPlanRow
import com.flux.ui.components.FluxEmptyState
import com.flux.ui.components.FluxErrorState
import com.flux.ui.components.FluxIconButton
import com.flux.ui.components.FluxPrimaryButton
import com.flux.ui.components.FluxSecondaryButton
import com.flux.ui.components.FluxSectionHeader
import com.flux.ui.components.FluxTimePicker
import com.flux.ui.components.FluxToggleRow
import com.flux.ui.components.FluxTopBar
import com.flux.ui.components.FutureNoteCard
import com.flux.ui.components.HabitRow
import com.flux.ui.components.NextRitualCard
import com.flux.ui.components.RitualCue
import com.flux.ui.components.RitualCueAnimation
import com.flux.ui.components.RitualStepRow
import com.flux.ui.components.RitualVisualCue
import com.flux.ui.components.TaskPlanRow
import com.flux.ui.foundation.FluxTheme
import com.flux.ui.navigation.FluxNavigationShell

@Preview(name = "Buttons - Light", showBackground = true, backgroundColor = 0xFFF7F8FC)
@Composable
fun ButtonsPreviewLight() {
    FluxTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FluxPrimaryButton(text = "Save ritual", onClick = {})
            FluxPrimaryButton(text = "Loading state", onClick = {}, isLoading = true)
            FluxPrimaryButton(text = "Disabled state", onClick = {}, enabled = false)
            FluxSecondaryButton(text = "Skip for now", onClick = {})
            FluxSecondaryButton(text = "Delete ritual", onClick = {}, isDestructive = true)
            Row {
                FluxIconButton(icon = Icons.Outlined.Settings, contentDescription = "Settings", onClick = {})
                FluxIconButton(icon = Icons.Default.Add, contentDescription = "Add", onClick = {})
            }
        }
    }
}

@Preview(name = "Buttons - Dark", showBackground = true, backgroundColor = 0xFF101216, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ButtonsPreviewDark() {
    FluxTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FluxPrimaryButton(text = "Save ritual", onClick = {})
            FluxSecondaryButton(text = "Skip for now", onClick = {})
            FluxSecondaryButton(text = "Delete ritual", onClick = {}, isDestructive = true)
        }
    }
}

@Preview(name = "Components Matrix - Light", showBackground = true, backgroundColor = 0xFFF7F8FC)
@Composable
fun ComponentsMatrixPreviewLight() {
    FluxTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            FluxTopBar(title = "Today", actionIcon = Icons.Outlined.Settings, onActionClick = {})

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                NextRitualCard(
                    title = "Weekday start",
                    timeText = "6:30 AM",
                    repeatSummary = "Mon, Tue, Wed, Thu, Fri",
                    cue = RitualVisualCue.Sunrise,
                    steps = listOf("Wake up", "Make breakfast", "Get ready"),
                    isEnabled = true,
                    onToggleEnabled = {},
                    onClick = {}
                )
            }

            Spacer(Modifier.height(12.dp))
            FutureNoteCard(noteText = "Take a breath before leaving today. Remember keys and umbrella.")

            FluxSectionHeader(title = "Habits")
            HabitRow(title = "Drink 500ml water", isCompletedToday = true, onToggleCompleted = {})
            HabitRow(title = "5-minute gentle stretch", isCompletedToday = false, onToggleCompleted = {})

            FluxSectionHeader(title = "Tasks")
            TaskPlanRow(title = "Pick up groceries", isCompleted = false, dueTime = "5:00 PM", onToggleCompleted = {})
            TaskPlanRow(title = "Review morning checklist", isCompleted = true, onToggleCompleted = {})

            FluxSectionHeader(title = "Events")
            EventPlanRow(title = "Design review", timeText = "10:00 AM", leaveByText = "9:30 AM")

            FluxSectionHeader(title = "Ritual steps")
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                RitualStepRow(stepNumber = 1, stepDescription = "Wake up gently")
                RitualStepRow(stepNumber = 2, stepDescription = "Drink cold water", isCompleted = true)
            }

            FluxSectionHeader(title = "Controls")
            FluxToggleRow(title = "Vibrate on start", checked = true, onCheckedChange = {})
        }
    }
}

@Preview(name = "Components Matrix - Dark", showBackground = true, backgroundColor = 0xFF101216, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ComponentsMatrixPreviewDark() {
    FluxTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            FluxTopBar(title = "Today", actionIcon = Icons.Outlined.Settings, onActionClick = {})
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                NextRitualCard(
                    title = "Weekday start",
                    timeText = "6:30 AM",
                    repeatSummary = "Mon, Tue, Wed, Thu, Fri",
                    cue = RitualVisualCue.Sunrise,
                    steps = listOf("Wake up", "Make breakfast", "Get ready"),
                    isEnabled = true,
                    onToggleEnabled = {},
                    onClick = {}
                )
            }
            Spacer(Modifier.height(12.dp))
            FutureNoteCard(noteText = "Take a breath before leaving today.")
            FluxSectionHeader(title = "Tasks")
            TaskPlanRow(title = "Pick up groceries", isCompleted = false, dueTime = "5:00 PM", onToggleCompleted = {})
            TaskPlanRow(title = "Review morning checklist", isCompleted = true, onToggleCompleted = {})
        }
    }
}

@Preview(name = "Named Cues Suite", showBackground = true, backgroundColor = 0xFFF7F8FC)
@Composable
fun CuesSuitePreview() {
    FluxTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RitualCue(cue = RitualVisualCue.Sunrise, useMorningWarmth = true)
                RitualCue(cue = RitualVisualCue.Breathe)
                RitualCue(cue = RitualVisualCue.Hydrate)
                RitualCue(cue = RitualVisualCue.Commute)
                RitualCue(cue = RitualVisualCue.Focus)
                RitualCue(cue = RitualVisualCue.Exercise)
                RitualCue(cue = RitualVisualCue.WindDown)
            }

            RitualCueAnimation(cue = RitualVisualCue.Sunrise)
            RitualCueAnimation(cue = RitualVisualCue.Breathe)

            FluxTimePicker(
                initialHour = 6,
                initialMinute = 30,
                onTimeSelected = { _, _ -> }
            )
        }
    }
}

@Preview(name = "States - Empty & Error", showBackground = true, backgroundColor = 0xFFF7F8FC)
@Composable
fun EmptyAndErrorStatesPreview() {
    FluxTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            FluxEmptyState(
                title = "No rituals yet",
                description = "Create a ritual alarm to connect waking up to a gentle morning flow.",
                actionText = "Create first ritual",
                onActionClick = {}
            )

            FluxErrorState(
                title = "Could not schedule alarm",
                message = "Exact alarm permission was revoked in device settings.",
                onRetry = {}
            )
        }
    }
}

@Preview(name = "Today Screen - Phone", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun TodayPhonePreview() {
    FluxTheme(darkTheme = false) {
        FluxNavigationShell()
    }
}

@Preview(name = "Today Screen - Tablet", showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
fun TodayTabletPreview() {
    FluxTheme(darkTheme = false) {
        FluxNavigationShell()
    }
}

@Preview(name = "Accessibility - Large Font (1.3x)", showBackground = true, fontScale = 1.3f, widthDp = 390, heightDp = 844)
@Composable
fun AccessibilityLargeFontPreview() {
    FluxTheme(darkTheme = false) {
        FluxNavigationShell()
    }
}

@Preview(name = "Accessibility - Reduce Motion", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun AccessibilityReduceMotionPreview() {
    FluxTheme(darkTheme = false, reduceMotion = true) {
        FluxNavigationShell()
    }
}

@Preview(name = "Rituals Screen - Flat Rows", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun RitualsScreenPreviewLight() {
    FluxTheme(darkTheme = false) {
        com.flux.ritual.ui.RitualsScreen(
            rituals = listOf(
                com.flux.ritual.data.RitualEntity(
                    id = "1",
                    title = "Weekday start",
                    hour = 6,
                    minute = 30,
                    repeatDays = setOf(1, 2, 3, 4, 5),
                    cue = RitualVisualCue.Sunrise,
                    steps = listOf("Wake up", "Make breakfast", "Get ready"),
                    isEnabled = true
                ),
                com.flux.ritual.data.RitualEntity(
                    id = "2",
                    title = "Weekend ease",
                    hour = 8,
                    minute = 15,
                    repeatDays = setOf(6, 7),
                    cue = RitualVisualCue.Hydrate,
                    steps = listOf("Stretch", "Pour coffee"),
                    isEnabled = false
                )
            ),
            canScheduleExact = true,
            onAddRitual = {},
            onEditRitual = {},
            onToggleEnabled = { _, _ -> },
            onRequestExactPermission = {}
        )
    }
}

@Preview(name = "Edit Ritual Screen - Light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun EditRitualScreenPreviewLight() {
    FluxTheme(darkTheme = false) {
        com.flux.ritual.ui.EditRitualScreen(
            initialRitual = com.flux.ritual.data.RitualEntity(
                id = "1",
                title = "Weekday start",
                hour = 6,
                minute = 30,
                repeatDays = setOf(1, 2, 3, 4, 5),
                cue = RitualVisualCue.Sunrise,
                steps = listOf("Wake up", "Make breakfast", "Get ready")
            ),
            onSave = {},
            onDelete = {},
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Ringing Alarm Screen - Phone", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun RingingAlarmScreenPhonePreview() {
    FluxTheme(darkTheme = false) {
        com.flux.ritual.ui.RingingAlarmScreen(
            ritual = com.flux.ritual.data.RitualEntity(
                id = "1",
                title = "Weekday start",
                hour = 6,
                minute = 30,
                repeatDays = setOf(1, 2, 3, 4, 5),
                cue = RitualVisualCue.Sunrise,
                steps = listOf("Wake up gently", "Drink cold water", "Morning walk")
            ),
            onStop = {},
            onSnooze = {}
        )
    }
}

@Preview(name = "Ringing Alarm Screen - Tablet", showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
fun RingingAlarmScreenTabletPreview() {
    FluxTheme(darkTheme = false) {
        com.flux.ritual.ui.RingingAlarmScreen(
            ritual = com.flux.ritual.data.RitualEntity(
                id = "1",
                title = "Weekday start",
                hour = 6,
                minute = 30,
                repeatDays = setOf(1, 2, 3, 4, 5),
                cue = RitualVisualCue.Sunrise,
                steps = listOf("Wake up gently", "Drink cold water", "Morning walk")
            ),
            onStop = {},
            onSnooze = {}
        )
    }
}

@Preview(name = "Ringing Alarm Screen - Dark", showBackground = true, widthDp = 390, heightDp = 844, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RingingAlarmScreenDarkPreview() {
    FluxTheme(darkTheme = true) {
        com.flux.ritual.ui.RingingAlarmScreen(
            ritual = com.flux.ritual.data.RitualEntity(
                id = "1",
                title = "Evening wind down",
                hour = 22,
                minute = 0,
                repeatDays = setOf(1, 2, 3, 4, 5),
                cue = RitualVisualCue.WindDown,
                steps = listOf("Dim the lights", "Read a chapter")
            ),
            onStop = {},
            onSnooze = {}
        )
    }
}

@Preview(name = "Ringing Alarm Screen - Reduce Motion", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun RingingAlarmScreenReduceMotionPreview() {
    FluxTheme(darkTheme = false, reduceMotion = true) {
        com.flux.ritual.ui.RingingAlarmScreen(
            ritual = com.flux.ritual.data.RitualEntity(
                id = "1",
                title = "Weekday start",
                hour = 6,
                minute = 30,
                repeatDays = setOf(1, 2, 3, 4, 5),
                cue = RitualVisualCue.Breathe,
                steps = listOf("Wake up gently")
            ),
            onStop = {},
            onSnooze = {}
        )
    }
}

@Preview(name = "Plan Screen - Light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PlanScreenPreviewLight() {
    FluxTheme(darkTheme = false) {
        com.flux.plan.ui.PlanScreen(
            tasks = listOf(
                com.flux.plan.data.TaskEntity(
                    id = "t1",
                    title = "Pick up groceries",
                    dueDateMillis = System.currentTimeMillis() + 18000000L,
                    isCompleted = false
                ),
                com.flux.plan.data.TaskEntity(
                    id = "t2",
                    title = "Review morning checklist",
                    dueDateMillis = null,
                    isCompleted = true
                )
            ),
            events = listOf(
                com.flux.plan.data.EventEntity(
                    id = "e1",
                    title = "Design review",
                    startTimeMillis = System.currentTimeMillis() + 7200000L,
                    bufferMinutes = 30
                )
            ),
            habits = listOf(
                com.flux.plan.data.HabitEntity(
                    id = "h1",
                    title = "Drink 500ml water",
                    lastCompletedDate = "2026-11-02"
                ),
                com.flux.plan.data.HabitEntity(
                    id = "h2",
                    title = "5-minute gentle stretch",
                    lastCompletedDate = null
                )
            ),
            todayDateString = "2026-11-02",
            onToggleTask = { _, _ -> },
            onToggleHabit = { _, _ -> },
            onAddTask = {},
            onAddEvent = {},
            onAddHabit = {},
            onEditTask = {},
            onEditEvent = {},
            onEditHabit = {}
        )
    }
}

@Preview(name = "Settings Screen - Light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun SettingsScreenPreviewLight() {
    FluxTheme(darkTheme = false) {
        com.flux.settings.SettingsScreen(
            settings = com.flux.settings.FluxAppSettings(
                themeMode = com.flux.settings.ThemeMode.SYSTEM,
                reduceMotion = false,
                defaultSnoozeMinutes = 9,
                defaultVibrate = true
            ),
            hasExactAlarm = true,
            hasNotification = true,
            onSetThemeMode = {},
            onSetReduceMotion = {},
            onSetDefaultSnooze = {},
            onSetDefaultVibrate = {},
            onNavigateBack = {}
        )
    }
}
