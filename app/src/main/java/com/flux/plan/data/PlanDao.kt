package com.flux.plan.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    // --- TASKS ---
    @Query("SELECT * FROM plan_tasks ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM plan_tasks WHERE (dueDateMillis IS NULL OR (dueDateMillis >= :startOfDay AND dueDateMillis <= :endOfDay)) AND isPostponed = 0 ORDER BY isCompleted ASC, createdAt ASC")
    fun getTasksDueToday(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: TaskEntity)

    @Query("UPDATE plan_tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTaskCompletion(id: String, isCompleted: Boolean)

    @Query("UPDATE plan_tasks SET isPostponed = :isPostponed WHERE id = :id")
    suspend fun updateTaskPostponed(id: String, isPostponed: Boolean)

    @Query("DELETE FROM plan_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)

    // --- EVENTS ---
    @Query("SELECT * FROM plan_events ORDER BY startTimeMillis ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM plan_events WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay ORDER BY startTimeMillis ASC")
    fun getEventsForDay(startOfDay: Long, endOfDay: Long): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEvent(event: EventEntity)

    @Query("DELETE FROM plan_events WHERE id = :id")
    suspend fun deleteEventById(id: String)

    // --- HABITS ---
    @Query("SELECT * FROM plan_habits ORDER BY isPaused ASC, createdAt ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM plan_habits WHERE isFocusedToday = 1 AND isPaused = 0 LIMIT 3")
    fun getFocusedHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHabit(habit: HabitEntity)

    @Query("UPDATE plan_habits SET lastCompletedDate = :completionDate WHERE id = :id")
    suspend fun updateHabitCompletion(id: String, completionDate: String?)

    @Query("UPDATE plan_habits SET isPaused = :isPaused WHERE id = :id")
    suspend fun updateHabitPaused(id: String, isPaused: Boolean)

    @Query("DELETE FROM plan_habits WHERE id = :id")
    suspend fun deleteHabitById(id: String)

    // --- FUTURE NOTES ---
    @Query("SELECT * FROM future_notes WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun getAllFutureNotes(): Flow<List<FutureNoteEntity>>

    @Query("SELECT * FROM future_notes WHERE isSurfaced = 1 AND isArchived = 0 LIMIT 1")
    fun getSurfacedNote(): Flow<FutureNoteEntity?>

    @Query("SELECT * FROM future_notes WHERE isSurfaced = 1 AND isArchived = 0 LIMIT 1")
    suspend fun getSurfacedNoteDirect(): FutureNoteEntity?

    @Query("UPDATE future_notes SET isSurfaced = 0")
    suspend fun clearAllSurfacedNotes()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFutureNote(note: FutureNoteEntity)

    @Query("UPDATE future_notes SET isArchived = 1, isSurfaced = 0 WHERE id = :id")
    suspend fun archiveFutureNote(id: String)

    @Query("DELETE FROM future_notes WHERE id = :id")
    suspend fun deleteFutureNoteById(id: String)
}
