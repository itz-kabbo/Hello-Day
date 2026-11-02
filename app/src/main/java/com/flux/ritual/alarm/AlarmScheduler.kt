package com.flux.ritual.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.flux.MainActivity
import com.flux.ritual.data.RitualEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val TAG = "AlarmScheduler"
        const val ACTION_FIRE_ALARM = "com.flux.ritual.ACTION_FIRE_ALARM"
        const val ACTION_SNOOZE_ALARM = "com.flux.ritual.ACTION_SNOOZE_ALARM"
        const val ACTION_STOP_ALARM = "com.flux.ritual.ACTION_STOP_ALARM"
        const val EXTRA_RITUAL_ID = "extra_ritual_id"
        const val EXTRA_IS_SNOOZE = "extra_is_snooze"
    }

    fun canScheduleExact(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun schedule(ritual: RitualEntity) {
        if (!ritual.isEnabled) {
            cancel(ritual.id)
            return
        }

        val triggerTimeMillis = calculateNextTriggerTime(ritual.hour, ritual.minute, ritual.repeatDays)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_FIRE_ALARM
            putExtra(EXTRA_RITUAL_ID, ritual.id)
        }

        val requestCode = ritual.id.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, MainActivity::class.java)
        val showPendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (canScheduleExact()) {
                val clockInfo = AlarmManager.AlarmClockInfo(triggerTimeMillis, showPendingIntent)
                alarmManager.setAlarmClock(clockInfo, pendingIntent)
                Log.d(TAG, "Scheduled exact alarm clock for ritual ${ritual.id} at $triggerTimeMillis")
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
                Log.w(TAG, "Exact alarm permission missing; scheduled setAndAllowWhileIdle for ritual ${ritual.id}")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException while scheduling alarm for ritual ${ritual.id}", e)
            try {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
            } catch (fallbackEx: Exception) {
                Log.e(TAG, "Fallback scheduling failed", fallbackEx)
            }
        }
    }

    fun scheduleSnooze(ritualId: String, snoozeMinutes: Int) {
        val triggerTimeMillis = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_FIRE_ALARM
            putExtra(EXTRA_RITUAL_ID, ritualId)
            putExtra(EXTRA_IS_SNOOZE, true)
        }

        val requestCode = (ritualId + "_snooze").hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, MainActivity::class.java)
        val showPendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (canScheduleExact()) {
                val clockInfo = AlarmManager.AlarmClockInfo(triggerTimeMillis, showPendingIntent)
                alarmManager.setAlarmClock(clockInfo, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule snooze", e)
        }
    }

    fun cancel(ritualId: String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_FIRE_ALARM
            putExtra(EXTRA_RITUAL_ID, ritualId)
        }
        val requestCode = ritualId.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

        // Also cancel any active snooze
        val snoozeRequestCode = (ritualId + "_snooze").hashCode()
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            snoozeRequestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (snoozePendingIntent != null) {
            alarmManager.cancel(snoozePendingIntent)
            snoozePendingIntent.cancel()
        }
    }

    fun calculateNextTriggerTime(hour: Int, minute: Int, repeatDays: Set<Int>): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (repeatDays.isEmpty()) {
            if (target.timeInMillis <= now.timeInMillis) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
            return target.timeInMillis
        }

        fun calendarToIsoDay(calDay: Int): Int = when (calDay) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }

        for (i in 0..7) {
            val checkCal = (target.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, i)
            }
            val isoDay = calendarToIsoDay(checkCal.get(Calendar.DAY_OF_WEEK))
            if (repeatDays.contains(isoDay)) {
                if (checkCal.timeInMillis > now.timeInMillis) {
                    return checkCal.timeInMillis
                }
            }
        }

        target.add(Calendar.DAY_OF_YEAR, 1)
        return target.timeInMillis
    }
}
