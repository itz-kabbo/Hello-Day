package com.flux.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val channel = NotificationChannel(
            "hello_day_channel",
            "Hello Day Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for Hello Day alarms and reminders"
        }
        manager.createNotificationChannel(channel)
    }
}
