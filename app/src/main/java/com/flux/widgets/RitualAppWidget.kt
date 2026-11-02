package com.flux.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.flux.MainActivity
import com.flux.plan.data.EventEntity
import com.flux.plan.data.HabitEntity
import com.flux.plan.data.PlanRepository
import com.flux.plan.data.TaskEntity
import com.flux.ritual.data.RitualEntity
import com.flux.ritual.data.RitualRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@EntryPoint
@InstallIn(SingletonComponent::class)
interface FluxWidgetEntryPoint {
    fun ritualRepository(): RitualRepository
    fun planRepository(): PlanRepository
}

class RitualAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RitualAppWidget()
}

class RitualAppWidget : GlanceAppWidget() {

    companion object {
        private val SMALL_SQUARE = DpSize(120.dp, 120.dp)
        private val MEDIUM_RECT = DpSize(220.dp, 120.dp)
        private val LARGE_RECT = DpSize(280.dp, 240.dp)
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(SMALL_SQUARE, MEDIUM_RECT, LARGE_RECT)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            FluxWidgetEntryPoint::class.java
        )

        val nextRitual = try {
            entryPoint.ritualRepository().enabledRituals.first().firstOrNull()
        } catch (_: Exception) { null }

        val events = try {
            entryPoint.planRepository().eventsForToday.first()
        } catch (_: Exception) { emptyList() }

        val habits = try {
            entryPoint.planRepository().focusedHabits.first()
        } catch (_: Exception) { emptyList() }

        val tasks = try {
            entryPoint.planRepository().tasksDueToday.first().take(3)
        } catch (_: Exception) { emptyList() }

        provideContent {
            GlanceTheme {
                val size = LocalSize.current
                val isSmall = size.width < 180.dp || size.height < 140.dp
                val isMedium = !isSmall && (size.width < 260.dp || size.height < 220.dp)

                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.widgetBackground)
                        .cornerRadius(16.dp)
                        .padding(12.dp)
                        .clickable(actionStartActivity<MainActivity>())
                ) {
                    if (isSmall) {
                        SmallRitualWidgetContent(nextRitual)
                    } else if (isMedium) {
                        MediumRitualWidgetContent(nextRitual, events.firstOrNull(), habits.firstOrNull())
                    } else {
                        LargeRitualWidgetContent(nextRitual, events, habits, tasks)
                    }
                }
            }
        }
    }
}

@GlanceComposable
@Composable
private fun SmallRitualWidgetContent(ritual: RitualEntity?) {
    val inkColor = GlanceTheme.colors.onSurface
    val secondaryInk = GlanceTheme.colors.secondary
    val accentColor = GlanceTheme.colors.primary

    Column(modifier = GlanceModifier.fillMaxSize()) {
        Text(
            text = "NEXT RITUAL",
            style = TextStyle(
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(GlanceModifier.height(4.dp))

        if (ritual != null) {
            val timeStr = formatWidgetTime(ritual.hour, ritual.minute)
            Text(
                text = timeStr,
                style = TextStyle(
                    color = inkColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(GlanceModifier.height(2.dp))
            Text(
                text = ritual.title,
                style = TextStyle(
                    color = secondaryInk,
                    fontSize = 12.sp
                ),
                maxLines = 1
            )
        } else {
            Text(
                text = "No ritual active",
                style = TextStyle(color = secondaryInk, fontSize = 14.sp)
            )
        }
    }
}

@GlanceComposable
@Composable
private fun MediumRitualWidgetContent(
    ritual: RitualEntity?,
    firstEvent: EventEntity?,
    firstHabit: HabitEntity?
) {
    val inkColor = GlanceTheme.colors.onSurface
    val secondaryInk = GlanceTheme.colors.secondary
    val accentColor = GlanceTheme.colors.primary

    Row(modifier = GlanceModifier.fillMaxSize()) {
        // Left column: Next Ritual
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = "NEXT RITUAL",
                style = TextStyle(
                    color = accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(GlanceModifier.height(2.dp))
            if (ritual != null) {
                Text(
                    text = formatWidgetTime(ritual.hour, ritual.minute),
                    style = TextStyle(
                        color = inkColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = ritual.title,
                    style = TextStyle(color = secondaryInk, fontSize = 11.sp),
                    maxLines = 1
                )
            } else {
                Text(
                    text = "No ritual active",
                    style = TextStyle(color = secondaryInk, fontSize = 13.sp)
                )
            }
        }

        Spacer(GlanceModifier.width(8.dp))

        // Right column: First event and habit
        Column(modifier = GlanceModifier.defaultWeight()) {
            if (firstEvent != null) {
                val timeStr = SimpleDateFormat("h:mm a", Locale.US).format(Date(firstEvent.startTimeMillis))
                val leaveBy = firstEvent.leaveByTimeMillis?.let {
                    " • Leave " + SimpleDateFormat("h:mm a", Locale.US).format(Date(it))
                } ?: ""
                Text(
                    text = firstEvent.title,
                    style = TextStyle(color = inkColor, fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    maxLines = 1
                )
                Text(
                    text = "$timeStr$leaveBy",
                    style = TextStyle(color = secondaryInk, fontSize = 10.sp),
                    maxLines = 1
                )
                Spacer(GlanceModifier.height(6.dp))
            }

            if (firstHabit != null) {
                Text(
                    text = "✓ ${firstHabit.title}",
                    style = TextStyle(color = inkColor, fontSize = 12.sp),
                    maxLines = 1
                )
            }
        }
    }
}

@GlanceComposable
@Composable
private fun LargeRitualWidgetContent(
    ritual: RitualEntity?,
    events: List<EventEntity>,
    habits: List<HabitEntity>,
    tasks: List<TaskEntity>
) {
    val inkColor = GlanceTheme.colors.onSurface
    val secondaryInk = GlanceTheme.colors.secondary
    val accentColor = GlanceTheme.colors.primary

    Column(modifier = GlanceModifier.fillMaxSize()) {
        // Top: Ritual bar
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "NEXT RITUAL",
                    style = TextStyle(color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                )
                if (ritual != null) {
                    Text(
                        text = "${formatWidgetTime(ritual.hour, ritual.minute)} — ${ritual.title}",
                        style = TextStyle(color = inkColor, fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                } else {
                    Text(text = "No ritual active", style = TextStyle(color = secondaryInk, fontSize = 14.sp))
                }
            }
        }

        Spacer(GlanceModifier.height(8.dp))

        // Middle: Events & Habits
        if (events.isNotEmpty()) {
            val event = events.first()
            val timeStr = SimpleDateFormat("h:mm a", Locale.US).format(Date(event.startTimeMillis))
            val leaveBy = event.leaveByTimeMillis?.let {
                " • Leave " + SimpleDateFormat("h:mm a", Locale.US).format(Date(it))
            } ?: ""
            Text(
                text = "Event: ${event.title} ($timeStr$leaveBy)",
                style = TextStyle(color = inkColor, fontSize = 12.sp),
                maxLines = 1
            )
            Spacer(GlanceModifier.height(4.dp))
        }

        if (habits.isNotEmpty()) {
            Text(
                text = "Habits: " + habits.joinToString(", ") { it.title },
                style = TextStyle(color = secondaryInk, fontSize = 11.sp),
                maxLines = 1
            )
            Spacer(GlanceModifier.height(4.dp))
        }

        // Bottom: Up to 3 tasks
        if (tasks.isNotEmpty()) {
            Spacer(GlanceModifier.height(4.dp))
            Text(
                text = "TASKS DUE TODAY",
                style = TextStyle(color = secondaryInk, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            )
            tasks.forEach { task ->
                Text(
                    text = "• ${task.title}",
                    style = TextStyle(color = inkColor, fontSize = 12.sp),
                    maxLines = 1
                )
            }
        }
    }
}

private fun formatWidgetTime(hour: Int, minute: Int): String {
    val isPm = hour >= 12
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format(Locale.US, "%d:%02d %s", displayHour, minute, if (isPm) "PM" else "AM")
}
