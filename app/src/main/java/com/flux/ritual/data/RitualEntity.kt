package com.flux.ritual.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flux.ui.components.RitualVisualCue
import java.util.UUID

@Entity(tableName = "rituals")
data class RitualEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val hour: Int,
    val minute: Int,
    val repeatDays: Set<Int> = emptySet(), // 1 = Monday, 7 = Sunday; empty = once
    val cue: RitualVisualCue = RitualVisualCue.Sunrise,
    val soundUri: String? = null,
    val vibrate: Boolean = true,
    val snoozeMinutes: Int = 9,
    val isEnabled: Boolean = true,
    val steps: List<String> = emptyList(), // Maximum 3 steps per canonical spec
    val createdAt: Long = System.currentTimeMillis()
)
