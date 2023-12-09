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
        private const val ACTION_PAUSE = "action_pause"
        private const val ACTION_RESUME = "action_resume"
        private const val ACTION_CANCEL = "action_cancel"

        private const val TIME_MILLIS_KEY = "time_millis"
        private const val NOTIFICATION_ID = 1

        fun start(context: Context, timeMillis: Long) {
            val startIntent = Intent(context, TimerService::class.java)
            startIntent.putExtra(TIME_MILLIS_KEY, timeMillis)
            context.startForegroundService(startIntent)
        }
    }

    private val timer: CountdownTimer = CountdownTimer(this)

    // Can't be "just" val, because context is null during the object initialization
    private val notificationManager: NotificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private val baseNotificationBuilder: NotificationCompat.Builder
        get() {
            val notificationChannel = NotificationChannel(
                "NOTIFICATION_CHANNEL_ID",
                "NOTIFICATION_CHANNEL_NAME",
                NotificationManager.IMPORTANCE_MIN
            )

            notificationManager.createNotificationChannel(notificationChannel)

            return NotificationCompat.Builder(this, "NOTIFICATION_CHANNEL_ID")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_timer)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
        }

    private val pausePendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            this, NOTIFICATION_ID,
            Intent(ACTION_PAUSE), PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val cancelPendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            this, NOTIFICATION_ID,
            Intent(ACTION_CANCEL), PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val buttonClickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_PAUSE) {
                TODO()
            }
        }
    }

    override fun onCreate() {
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        ContextCompat.registerReceiver(
            applicationContext,
            buttonClickReceiver,
            IntentFilter(ACTION_PAUSE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        super.onCreate()
    }

    override fun onDestroy() {
        unregisterReceiver(buttonClickReceiver)
        timer.reset()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer.reset()

        val timeSeconds = (intent?.getLongExtra(TIME_MILLIS_KEY, 0L) ?: 0) / 1000L

        timer.setAndStart(timeMillis = timeSeconds * 1000)

        return START_STICKY
    }

    override fun onTick(leftTimeMillis: Long) {
        val notification = baseNotificationBuilder
            .addAction(0, "Cancel", cancelPendingIntent)
            .addAction(0, "Pause", pausePendingIntent)
            .setContentTitle("Time left: $leftTimeMillis ms")
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onTimeOver() {
        baseNotificationBuilder.setContentTitle("Time is over!")
        notificationManager.notify(NOTIFICATION_ID, baseNotificationBuilder.build())
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}