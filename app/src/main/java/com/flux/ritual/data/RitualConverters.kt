package com.flux.ritual.data

import androidx.room.TypeConverter
import com.flux.ui.components.RitualVisualCue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RitualConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromRepeatDays(days: Set<Int>?): String {
        return days?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toRepeatDays(data: String?): Set<Int> {
        if (data.isNullOrBlank()) return emptySet()
        return data.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }

    @TypeConverter
    fun fromStepsList(steps: List<String>?): String {
        return gson.toJson(steps ?: emptyList<String>())
    }

    @TypeConverter
    fun toStepsList(data: String?): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(data, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromRitualCue(cue: RitualVisualCue?): String {
        return (cue ?: RitualVisualCue.Sunrise).name
    }

    @TypeConverter
    fun toRitualCue(name: String?): RitualVisualCue {
        if (name.isNullOrBlank()) return RitualVisualCue.Sunrise
        return when (name) {
            "Walk" -> RitualVisualCue.Commute
            "Rest" -> RitualVisualCue.WindDown
            else -> try {
                RitualVisualCue.valueOf(name)
            } catch (_: Exception) {
                RitualVisualCue.Sunrise
            }
        }
    }
}
