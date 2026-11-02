package com.flux.plan.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanRepository @Inject constructor(
    private val planDao: PlanDao
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val todayDateString: String
        get() = dateFormat.format(Date())

    private fun getStartOfDay(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun getEndOfDay(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    // --- Tasks ---
    val allTasks: Flow<List<TaskEntity>> = planDao.getAllTasks()
    val tasksDueToday: Flow<List<TaskEntity>>
        get() = planDao.getTasksDueToday(getStartOfDay(), getEndOfDay())

    suspend fun saveTask(task: TaskEntity) = planDao.upsertTask(task)
    suspend fun toggleTaskCompletion(id: String, isCompleted: Boolean) = planDao.updateTaskCompletion(id, isCompleted)
    suspend fun postponeTask(id: String) = planDao.updateTaskPostponed(id, true)
    suspend fun deleteTask(id: String) = planDao.deleteTaskById(id)

    // --- Events ---
    val allEvents: Flow<List<EventEntity>> = planDao.getAllEvents()
    val eventsForToday: Flow<List<EventEntity>>
        get() = planDao.getEventsForDay(getStartOfDay(), getEndOfDay())

    suspend fun saveEvent(event: EventEntity) = planDao.upsertEvent(event)
    suspend fun deleteEvent(id: String) = planDao.deleteEventById(id)

    // --- Habits ---
    val allHabits: Flow<List<HabitEntity>> = planDao.getAllHabits()
    val focusedHabits: Flow<List<HabitEntity>> = planDao.getFocusedHabits()

    suspend fun saveHabit(habit: HabitEntity) = planDao.upsertHabit(habit)
    suspend fun toggleHabitCompletionToday(id: String, isCompletedCurrently: Boolean) {
        val completion = if (isCompletedCurrently) null else todayDateString
        planDao.updateHabitCompletion(id, completion)
    }
    suspend fun toggleHabitPaused(id: String, isPausedCurrently: Boolean) {
        planDao.updateHabitPaused(id, !isPausedCurrently)
    }
    suspend fun deleteHabit(id: String) = planDao.deleteHabitById(id)

    // --- Future Notes ---
    val surfacedNote: Flow<FutureNoteEntity?> = planDao.getSurfacedNote()
    val allFutureNotes: Flow<List<FutureNoteEntity>> = planDao.getAllFutureNotes()

    suspend fun setFutureNote(content: String) {
        planDao.clearAllSurfacedNotes()
        planDao.upsertFutureNote(FutureNoteEntity(content = content, isSurfaced = true))
    }
    suspend fun archiveFutureNote(id: String) = planDao.archiveFutureNote(id)
    suspend fun deleteFutureNote(id: String) = planDao.deleteFutureNoteById(id)
}
