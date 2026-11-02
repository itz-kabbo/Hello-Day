package com.flux.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flux.plan.data.EventEntity
import com.flux.plan.data.FutureNoteEntity
import com.flux.plan.data.HabitEntity
import com.flux.plan.data.PlanDao
import com.flux.plan.data.TaskEntity
import com.flux.ritual.data.RitualConverters
import com.flux.ritual.data.RitualDao
import com.flux.ritual.data.RitualEntity

@Database(
    entities = [
        RitualEntity::class,
        TaskEntity::class,
        EventEntity::class,
        HabitEntity::class,
        FutureNoteEntity::class
    ],
    version = 16,
    exportSchema = false
)
@TypeConverters(RitualConverters::class)
abstract class FluxDatabase : RoomDatabase() {
    abstract val ritualDao: RitualDao
    abstract val planDao: PlanDao
}
