package com.shpak.quicktimer.presentation

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
import androidx.core.content.ContextCompat
import com.shpak.quicktimer.R
import com.shpak.quicktimer.timer.CountdownTimer
import com.shpak.quicktimer.timer.TimerListener

class TimerService : Service(), TimerListener {
    companion object {
        private const val ACTION_START = "actionStart"
        private const val ACTION_RESUME = "actionResume"
        private const val ACTION_RESET = "actionReset"

        private const val TIME_MILLIS_KEY = "timeMillis"
        private const val NOTIFICATION_ID = 1

        fun start(context: Context, timeMillis: Long) {
            val startIntent = Intent(context, TimerService::class.java)
            startIntent.putExtra("timeMillis", timeMillis)
            context.startForegroundService(startIntent)
        }
    }

    private val timer: CountdownTimer = CountdownTimer(this)

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private val buttonClickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_START) {
                TODO()
            }
        }
    }

    override fun onCreate() {
        startAsForeground()

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        ContextCompat.registerReceiver(
            applicationContext,
            buttonClickReceiver,
            IntentFilter(ACTION_START),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        super.onCreate()
    }

    override fun onDestroy() {
        unregisterReceiver(buttonClickReceiver)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer.reset()

        val timeSeconds = (intent?.getLongExtra(TIME_MILLIS_KEY, 0L) ?: 0) / 1000L

        timer.setAndStart(timeMillis = timeSeconds * 1000)

        return START_STICKY
    }

    override fun onTick(leftTimeMillis: Long) {
        notificationBuilder.setContentTitle("Time left: $leftTimeMillis ms")
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onTimeOver() {
        notificationBuilder.setContentTitle("Time is over!")
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun startAsForeground() {
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

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        notificationBuilder =
            NotificationCompat.Builder(this, "NOTIFICATION_CHANNEL_ID")
                .setOngoing(true)
                .setContentTitle("0")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .addAction(R.drawable.ic_launcher_foreground, "Start", startPendingIntent)

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}