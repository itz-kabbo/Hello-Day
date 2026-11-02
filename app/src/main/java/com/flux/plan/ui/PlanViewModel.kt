package com.flux.plan.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flux.plan.data.EventEntity
import com.flux.plan.data.FutureNoteEntity
import com.flux.plan.data.HabitEntity
import com.flux.plan.data.PlanRepository
import com.flux.plan.data.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val repository: PlanRepository
) : ViewModel() {

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasksDueToday: StateFlow<List<TaskEntity>> = repository.tasksDueToday
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val events: StateFlow<List<EventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val eventsForToday: StateFlow<List<EventEntity>> = repository.eventsForToday
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<HabitEntity>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val focusedHabits: StateFlow<List<HabitEntity>> = repository.focusedHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val surfacedNote: StateFlow<FutureNoteEntity?> = repository.surfacedNote
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayDateString: String get() = repository.todayDateString

    // --- Task Actions ---
    fun saveTask(task: TaskEntity) = viewModelScope.launch { repository.saveTask(task) }
    fun toggleTaskCompletion(id: String, isCompleted: Boolean) = viewModelScope.launch { repository.toggleTaskCompletion(id, isCompleted) }
    fun postponeTask(id: String) = viewModelScope.launch { repository.postponeTask(id) }
    fun deleteTask(id: String) = viewModelScope.launch { repository.deleteTask(id) }

    // --- Event Actions ---
    fun saveEvent(event: EventEntity) = viewModelScope.launch { repository.saveEvent(event) }
    fun deleteEvent(id: String) = viewModelScope.launch { repository.deleteEvent(id) }

    // --- Habit Actions ---
    fun saveHabit(habit: HabitEntity) = viewModelScope.launch { repository.saveHabit(habit) }
    fun toggleHabitCompletionToday(id: String, isCompletedCurrently: Boolean) =
        viewModelScope.launch { repository.toggleHabitCompletionToday(id, isCompletedCurrently) }
    fun toggleHabitPaused(id: String, isPausedCurrently: Boolean) =
        viewModelScope.launch { repository.toggleHabitPaused(id, isPausedCurrently) }
    fun deleteHabit(id: String) = viewModelScope.launch { repository.deleteHabit(id) }

    // --- Future Note Actions ---
    fun setFutureNote(content: String) = viewModelScope.launch { repository.setFutureNote(content) }
    fun archiveFutureNote(id: String) = viewModelScope.launch { repository.archiveFutureNote(id) }
}
