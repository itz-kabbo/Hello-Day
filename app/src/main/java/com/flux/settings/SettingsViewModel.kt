package com.flux.settings

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: FluxSettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val settings: StateFlow<FluxAppSettings> = settingsRepository.settings

    fun setThemeMode(mode: ThemeMode) = settingsRepository.setThemeMode(mode)
    fun setReduceMotion(reduce: Boolean) = settingsRepository.setReduceMotion(reduce)
    fun setDefaultSnoozeMinutes(minutes: Int) = settingsRepository.setDefaultSnoozeMinutes(minutes)
    fun setDefaultVibrate(vibrate: Boolean) = settingsRepository.setDefaultVibrate(vibrate)

    fun hasExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
