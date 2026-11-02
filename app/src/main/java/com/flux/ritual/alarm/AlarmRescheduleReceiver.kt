package com.flux.ritual.alarm

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.flux.ritual.data.RitualRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmRescheduleReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: RitualRepository

    companion object {
        const val TAG = "AlarmRescheduleReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "Received broadcast action: $action")

        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                Log.d(TAG, "Rescheduling all enabled ritual alarms")
                repository.rescheduleAllEnabledAlarms()
            }
        }
    }
}
