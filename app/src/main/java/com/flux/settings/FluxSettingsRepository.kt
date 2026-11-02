package com.flux.settings

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class FluxAppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val reduceMotion: Boolean = false,
    val defaultSnoozeMinutes: Int = 9,
    val defaultVibrate: Boolean = true
)

@Singleton
class FluxSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("flux_app_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<FluxAppSettings> = _settings.asStateFlow()

    private fun loadSettings(): FluxAppSettings {
        val themeStr = prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        val themeMode = try {
            ThemeMode.valueOf(themeStr)
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
        val reduceMotion = prefs.getBoolean("reduce_motion", false)
        val snooze = prefs.getInt("default_snooze", 9)
        val vibrate = prefs.getBoolean("default_vibrate", true)

        return FluxAppSettings(
            themeMode = themeMode,
            reduceMotion = reduceMotion,
            defaultSnoozeMinutes = snooze,
            defaultVibrate = vibrate
        )
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun setReduceMotion(reduce: Boolean) {
        prefs.edit().putBoolean("reduce_motion", reduce).apply()
        _settings.value = _settings.value.copy(reduceMotion = reduce)
    }

    fun setDefaultSnoozeMinutes(minutes: Int) {
        prefs.edit().putInt("default_snooze", minutes).apply()
        _settings.value = _settings.value.copy(defaultSnoozeMinutes = minutes)
    }

    fun setDefaultVibrate(vibrate: Boolean) {
        prefs.edit().putBoolean("default_vibrate", vibrate).apply()
        _settings.value = _settings.value.copy(defaultVibrate = vibrate)
    }
}
