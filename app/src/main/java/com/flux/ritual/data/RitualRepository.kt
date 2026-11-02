package com.flux.ritual.data

import com.flux.ritual.alarm.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RitualRepository @Inject constructor(
    private val ritualDao: RitualDao,
    private val alarmScheduler: AlarmScheduler
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    val allRituals: Flow<List<RitualEntity>> = ritualDao.getAllRituals()
    val enabledRituals: Flow<List<RitualEntity>> = ritualDao.getEnabledRituals()

    fun getRitualById(id: String): Flow<RitualEntity?> = ritualDao.getRitualById(id)

    suspend fun getRitualByIdDirect(id: String): RitualEntity? = ritualDao.getRitualByIdDirect(id)

    suspend fun saveRitual(ritual: RitualEntity) {
        ritualDao.upsertRitual(ritual)
        if (ritual.isEnabled) {
            alarmScheduler.schedule(ritual)
        } else {
            alarmScheduler.cancel(ritual.id)
        }
    }

    suspend fun toggleEnabled(id: String, isEnabled: Boolean) {
        ritualDao.updateEnabled(id, isEnabled)
        val ritual = ritualDao.getRitualByIdDirect(id)
        if (ritual != null) {
            val updated = ritual.copy(isEnabled = isEnabled)
            if (isEnabled) {
                alarmScheduler.schedule(updated)
            } else {
                alarmScheduler.cancel(id)
            }
        }
    }

    suspend fun deleteRitual(id: String) {
        alarmScheduler.cancel(id)
        ritualDao.deleteRitualById(id)
    }

    suspend fun duplicateRitual(ritual: RitualEntity) {
        val duplicated = ritual.copy(
            id = java.util.UUID.randomUUID().toString(),
            title = "${ritual.title} (Copy)",
            isEnabled = false,
            createdAt = System.currentTimeMillis()
        )
        ritualDao.upsertRitual(duplicated)
    }

    fun rescheduleAllEnabledAlarms() {
        scope.launch {
            val enabledList = ritualDao.getEnabledRitualsDirect()
            for (ritual in enabledList) {
                alarmScheduler.schedule(ritual)
            }
        }
    }

    fun snooze(ritualId: String, snoozeMinutes: Int) {
        alarmScheduler.scheduleSnooze(ritualId, snoozeMinutes)
    }
}
