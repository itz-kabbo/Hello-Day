package com.flux.ritual.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.flux.MainActivity
import com.flux.R
import com.flux.ritual.data.RitualRepository
import com.flux.ritual.ui.AlarmActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service() {

    @Inject
    lateinit var repository: RitualRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main)

    companion object {
        const val TAG = "AlarmService"
        const val CHANNEL_ID = "flux_ritual_channel"
        const val NOTIFICATION_ID = 4001

        const val ACTION_START = "com.flux.ritual.START"
        const val ACTION_STOP = "com.flux.ritual.STOP"
        const val ACTION_SNOOZE = "com.flux.ritual.SNOOZE"

        const val EXTRA_RITUAL_ID = "extra_ritual_id"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY
        val ritualId = intent.getStringExtra(EXTRA_RITUAL_ID) ?: ""

        when (action) {
            ACTION_START -> {
                serviceScope.launch {
                    val ritual = repository.getRitualByIdDirect(ritualId)
                    val title = ritual?.title ?: "Ritual Alarm"
                    val snoozeMinutes = ritual?.snoozeMinutes ?: 9
                    val playSound = true
                    val vibrate = ritual?.vibrate ?: true
                    val soundUriStr = ritual?.soundUri

                    startForegroundWithNotification(ritualId, title, snoozeMinutes)
                    if (playSound) startRingtone(soundUriStr)
                    if (vibrate) startVibration()

                    // Also launch full-screen AlarmActivity directly
                    val activityIntent = Intent(applicationContext, AlarmActivity::class.java).apply {
                        this.action = AlarmActivity.ACTION_RINGING
                        putExtra(AlarmActivity.EXTRA_RITUAL_ID, ritualId)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    }
                    startActivity(activityIntent)
                }
            }
            ACTION_STOP -> {
                stopAlarmAndRelease(ritualId, launchMorningContext = true)
            }
            ACTION_SNOOZE -> {
                serviceScope.launch {
                    val ritual = repository.getRitualByIdDirect(ritualId)
                    val snoozeMinutes = ritual?.snoozeMinutes ?: 9
                    repository.snooze(ritualId, snoozeMinutes)
                    stopAlarmAndRelease(ritualId, launchMorningContext = false)
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun startForegroundWithNotification(ritualId: String, title: String, snoozeMinutes: Int) {
        val fullScreenIntent = Intent(this, AlarmActivity::class.java).apply {
            action = AlarmActivity.ACTION_RINGING
            putExtra(AlarmActivity.EXTRA_RITUAL_ID, ritualId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            ritualId.hashCode(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_STOP
            putExtra(EXTRA_RITUAL_ID, ritualId)
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            (ritualId + "_stop").hashCode(),
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_RITUAL_ID, ritualId)
        }
        val snoozePendingIntent = PendingIntent.getService(
            this,
            (ritualId + "_snooze").hashCode(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText("Ritual sequence ready")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .addAction(0, "Snooze ($snoozeMinutes m)", snoozePendingIntent)
            .addAction(0, "Stop", stopPendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun startRingtone(soundUriStr: String?) {
        try {
            val uri = if (!soundUriStr.isNullOrEmpty()) {
                Uri.parse(soundUriStr)
            } else {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, uri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing alarm sound", e)
        }
    }

    private fun startVibration() {
        try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            val timings = longArrayOf(0, 600, 400, 600, 800)
            val amplitudes = intArrayOf(0, 180, 0, 220, 0)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, 0)
            vibrator?.vibrate(effect)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting vibration", e)
        }
    }

    private fun stopAlarmAndRelease(ritualId: String, launchMorningContext: Boolean) {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping media player", e)
        }

        try {
            vibrator?.cancel()
            vibrator = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping vibrator", e)
        }

        // Send local broadcast to dismiss AlarmActivity if currently visible
        val dismissIntent = Intent(AlarmActivity.ACTION_DISMISS)
        sendBroadcast(dismissIntent)

        if (launchMorningContext) {
            serviceScope.launch {
                val ritual = repository.getRitualByIdDirect(ritualId)
                if (ritual != null) {
                    if (ritual.repeatDays.isEmpty()) {
                        // One-time alarm, disable after ring
                        repository.saveRitual(ritual.copy(isEnabled = false))
                    } else {
                        // Schedule next occurrence
                        alarmScheduler.schedule(ritual)
                    }
                }
            }

            val morningIntent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(morningIntent)
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        val name = "Ritual Alarms"
        val descriptionText = "Sound and visual alerts for gentle wake-up sequences"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            enableVibration(true)
            setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        try {
            mediaPlayer?.release()
            mediaPlayer = null
            vibrator?.cancel()
            vibrator = null
        } catch (_: Exception) {}
        serviceScope.cancel()
        super.onDestroy()
    }
}
