package com.shpak.quicktimer

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.CATEGORY_DEFAULT
import android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.service.quicksettings.TileService
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.shpak.quicktimer.dialog.NotificationPermissionRequestDialog
import java.util.Timer
import java.util.TimerTask

class TimerTileService : TileService() {
    companion object {
        private const val ACTION_START = "actionStart"
        private const val ACTION_RESUME = "actionResume"
        private const val ACTION_RESET = "actionReset"
        private const val NOTIFICATION_ID = 0
    }

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private val buttonClickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_START) {
                var seconds = 0
                Timer().scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        notificationBuilder.setContentTitle("$seconds")
                        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                        seconds++
                    }
                }, 0, 1000)
            }
        }
    }

    override fun onCreate() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        registerReceiver(buttonClickReceiver, IntentFilter(ACTION_START))
        super.onCreate()
    }

    override fun onClick() {
        super.onClick()

        NotificationPermissionRequestDialog.show(this)
    }

    override fun onDestroy() {
        unregisterReceiver(buttonClickReceiver)
        super.onDestroy()
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

    private fun isNotificationsPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun showNotification() {
        val startIntent = Intent(ACTION_START)
        startIntent.action = ACTION_START
        val startPendingIntent = PendingIntent.getBroadcast(
            this, NOTIFICATION_ID,
            startIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notificationChannel = NotificationChannel(
            "NOTIFICATION_CHANNEL_ID",
            "NOTIFICATION_CHANNEL_NAME",
            NotificationManager.IMPORTANCE_MIN
        )

        notificationManager.createNotificationChannel(notificationChannel)

        notificationBuilder =
            NotificationCompat.Builder(this, "NOTIFICATION_CHANNEL_ID").setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .addAction(R.drawable.ic_launcher_foreground, "Start", startPendingIntent)

        notificationBuilder.setContentTitle("0")
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}