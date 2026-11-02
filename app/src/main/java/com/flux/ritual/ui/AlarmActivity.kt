package com.flux.ritual.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.flux.ritual.alarm.AlarmService
import com.flux.ritual.data.RitualRepository
import com.flux.ui.foundation.FluxTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {

    @Inject
    lateinit var repository: RitualRepository

    private var ritualId: String = ""

    companion object {
        const val ACTION_RINGING = "com.flux.ritual.ACTION_RINGING"
        const val ACTION_DISMISS = "com.flux.ritual.ACTION_DISMISS"
        const val EXTRA_RITUAL_ID = "extra_ritual_id"
    }

    private val dismissReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ritualId = intent.getStringExtra(EXTRA_RITUAL_ID) ?: ""

        // Wake screen and show over keyguard/lockscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val filter = IntentFilter(ACTION_DISMISS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(dismissReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(dismissReceiver, filter)
        }

        enableEdgeToEdge()

        setContent {
            val isDark = isSystemInDarkTheme()
            val ritual by repository.getRitualById(ritualId).collectAsState(initial = null)

            FluxTheme(darkTheme = isDark) {
                RingingAlarmScreen(
                    ritual = ritual,
                    onStop = {
                        val stopIntent = Intent(this, AlarmService::class.java).apply {
                            action = AlarmService.ACTION_STOP
                            putExtra(AlarmService.EXTRA_RITUAL_ID, ritualId)
                        }
                        startService(stopIntent)
                        finish()
                    },
                    onSnooze = {
                        val snoozeIntent = Intent(this, AlarmService::class.java).apply {
                            action = AlarmService.ACTION_SNOOZE
                            putExtra(AlarmService.EXTRA_RITUAL_ID, ritualId)
                        }
                        startService(snoozeIntent)
                        finish()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(dismissReceiver)
        } catch (_: Exception) {}
        super.onDestroy()
    }
}
