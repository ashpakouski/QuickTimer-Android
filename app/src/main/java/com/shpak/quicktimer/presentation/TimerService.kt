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
            return NotificationCompat.Builder(applicationContext, "NOTIFICATION_CHANNEL_ID")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_timer)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
        }

    private val pausePendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            applicationContext, NOTIFICATION_ID,
            Intent(ACTION_PAUSE), PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val cancelPendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            applicationContext, NOTIFICATION_ID,
            Intent(ACTION_CANCEL), PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val resumePendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            applicationContext, NOTIFICATION_ID,
            Intent(ACTION_RESUME), PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val buttonClickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_PAUSE -> {
                    timer.pause()
                    onTimerPause()
                }
                ACTION_RESUME -> timer.resume()
                ACTION_CANCEL -> {
                    timer.cancel()
                    stopSelf()
                }
            }
        }
    }

    private fun createNotificationChannel() {
        NotificationChannel(
            "NOTIFICATION_CHANNEL_ID",
            "NOTIFICATION_CHANNEL_NAME",
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            notificationManager.createNotificationChannel(this)
        }
    }

    override fun onCreate() {
        createNotificationChannel()

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        ContextCompat.registerReceiver(
            applicationContext,
            buttonClickReceiver,
            IntentFilter().apply {
                addAction(ACTION_PAUSE)
                addAction(ACTION_RESUME)
                addAction(ACTION_CANCEL)
            },
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        super.onCreate()
    }

    override fun onDestroy() {
        unregisterReceiver(buttonClickReceiver)
        timer.cancel()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer.cancel()

        val timeMillis = intent?.getLongExtra(TIME_MILLIS_KEY, 0L) ?: 0

        timer.setAndStart(timeMillis = timeMillis)

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

    fun onTimerPause() {
        val notification = baseNotificationBuilder
            .addAction(0, "Cancel", cancelPendingIntent)
            .addAction(0, "Resume", resumePendingIntent)
            .setContentTitle("Time left: ${timer.millisLeft} ms")
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