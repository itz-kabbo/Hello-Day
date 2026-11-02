package com.flux.ritual.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flux.ritual.alarm.AlarmScheduler
import com.flux.ritual.data.RitualEntity
import com.flux.ritual.data.RitualRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RitualsViewModel @Inject constructor(
    private val repository: RitualRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    val rituals: StateFlow<List<RitualEntity>> = repository.allRituals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleEnabled(id: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleEnabled(id, enabled)
        }
    }

    fun saveRitual(ritual: RitualEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveRitual(ritual)
            onComplete()
        }
    }

    fun deleteRitual(id: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteRitual(id)
            onComplete()
        }
    }

    fun duplicateRitual(ritual: RitualEntity) {
        viewModelScope.launch {
            repository.duplicateRitual(ritual)
        }
    }

    fun canScheduleExact(): Boolean = alarmScheduler.canScheduleExact()
}
