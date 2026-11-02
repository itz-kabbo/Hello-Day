package com.flux.ritual.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RitualDao {
    @Query("SELECT * FROM rituals ORDER BY hour ASC, minute ASC")
    fun getAllRituals(): Flow<List<RitualEntity>>

    @Query("SELECT * FROM rituals WHERE isEnabled = 1 ORDER BY hour ASC, minute ASC")
    fun getEnabledRituals(): Flow<List<RitualEntity>>

    @Query("SELECT * FROM rituals WHERE isEnabled = 1 ORDER BY hour ASC, minute ASC")
    suspend fun getEnabledRitualsDirect(): List<RitualEntity>

    @Query("SELECT * FROM rituals WHERE id = :id LIMIT 1")
    fun getRitualById(id: String): Flow<RitualEntity?>

    @Query("SELECT * FROM rituals WHERE id = :id LIMIT 1")
    suspend fun getRitualByIdDirect(id: String): RitualEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRitual(ritual: RitualEntity)

    @Delete
    suspend fun deleteRitual(ritual: RitualEntity)

    @Query("DELETE FROM rituals WHERE id = :id")
    suspend fun deleteRitualById(id: String)

    @Query("UPDATE rituals SET isEnabled = :enabled WHERE id = :id")
    suspend fun updateEnabled(id: String, enabled: Boolean)
}
