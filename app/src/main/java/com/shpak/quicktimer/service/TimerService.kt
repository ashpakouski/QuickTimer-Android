package com.shpak.quicktimer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.shpak.quicktimer.R
import java.util.Timer
import java.util.TimerTask

class TimerService : Service() {
    companion object {
        private const val ACTION_START = "actionStart"
        private const val ACTION_RESUME = "actionResume"
        private const val ACTION_RESET = "actionReset"
        private const val NOTIFICATION_ID = 0

        fun start(context: Context) {

        }
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

    override fun onDestroy() {
        unregisterReceiver(buttonClickReceiver)
        super.onDestroy()
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}