package com.flux.plan.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "plan_tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val dueDateMillis: Long? = null,
    val isCompleted: Boolean = false,
    val isPostponed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "plan_events")
data class EventEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long = startTimeMillis + (60 * 60 * 1000L),
    val bufferMinutes: Int = 0, // Preparation or travel buffer in minutes
    val createdAt: Long = System.currentTimeMillis()
) {
    val leaveByTimeMillis: Long?
        get() = if (bufferMinutes > 0) startTimeMillis - (bufferMinutes * 60 * 1000L) else null
}

@Entity(tableName = "plan_habits")
data class HabitEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isPaused: Boolean = false,
    val isFocusedToday: Boolean = true, // Included in Today morning context (up to 3)
    val lastCompletedDate: String? = null, // "yyyy-MM-dd"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "future_notes")
data class FutureNoteEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isSurfaced: Boolean = true, // The note currently shown on Today screen
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
