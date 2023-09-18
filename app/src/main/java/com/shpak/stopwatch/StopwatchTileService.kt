package com.shpak.stopwatch

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.Intent.CATEGORY_DEFAULT
import android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.service.quicksettings.TileService
import androidx.core.app.NotificationCompat
import java.util.Timer
import java.util.TimerTask

class StopwatchTileService : TileService() {
    override fun onClick() {
        super.onClick()

        showNotification()
    }

    private fun redirectToAppSettings() {
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
        with(intent) {
            data = Uri.fromParts("package", packageName, null)
            addCategory(CATEGORY_DEFAULT)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }

        startActivity(intent)
    }

    private fun showNotification() {
        val notificationChannel = NotificationChannel(
            "NOTIFICATION_CHANNEL_ID",
            "NOTIFICATION_CHANNEL_NAME",
            NotificationManager.IMPORTANCE_MIN
        )

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        val notification =
            NotificationCompat.Builder(this, "NOTIFICATION_CHANNEL_ID").setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)

        var seconds = 0
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                notification.setContentTitle("$seconds")
                notificationManager.notify(0, notification.build())
                seconds++
            }
        }, 0, 1000)
    }
}