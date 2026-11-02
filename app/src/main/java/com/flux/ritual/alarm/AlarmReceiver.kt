package com.flux.ritual.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val ritualId = intent.getStringExtra(AlarmScheduler.EXTRA_RITUAL_ID) ?: ""
        Log.d(TAG, "Alarm triggered for ritual: $ritualId")

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "flux:alarm_trigger_wake_lock"
        )
        wakeLock.acquire(10_000L) // 10 seconds timeout

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_START
            putExtra(AlarmService.EXTRA_RITUAL_ID, ritualId)
        }

        try {
            context.startForegroundService(serviceIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start foreground AlarmService", e)
        } finally {
            if (wakeLock.isHeld) {
                try {
                    wakeLock.release()
                } catch (_: Exception) {}
            }
        }
    }
}
