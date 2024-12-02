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
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.shpak.quicktimer.R
import com.shpak.quicktimer.data.CountdownTimer
import com.shpak.quicktimer.data.TimerAlreadyRunningException
import com.shpak.quicktimer.data.TimerListener
import com.shpak.quicktimer.util.playSound
import com.shpak.quicktimer.util.toHhMmSs

class TimerService : Service(), TimerListener {
    companion object {
        private const val ACTION_PAUSE = "action_pause"
        private const val ACTION_RESUME = "action_resume"
        private const val ACTION_CANCEL = "action_cancel"

        private const val TIME_MILLIS_KEY = "time_millis"
        private const val NOTIFICATION_ID = 1
        private const val PENDING_INTENT_REQUEST_CODE = 2

        fun start(context: Context, timeMillis: Long) {
            val startIntent = Intent(context, TimerService::class.java)
            startIntent.putExtra(TIME_MILLIS_KEY, timeMillis)
            context.startForegroundService(startIntent)
        }
    }

    private val timer = CountdownTimer(this, this)

    // Can't be "just" val, because context is null during the object initialization
    private val notificationManager: NotificationManager? by lazy {
        getSystemService(NotificationManager::class.java)
    }

    private val baseNotificationBuilder: NotificationCompat.Builder
        get() = NotificationCompat
            .Builder(applicationContext, getString(R.string.notification_channel_id))
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_timer)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)

    private val pausePendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            applicationContext, PENDING_INTENT_REQUEST_CODE,
            Intent(ACTION_PAUSE).appendPackageName(), PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val cancelPendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            applicationContext, PENDING_INTENT_REQUEST_CODE,
            Intent(ACTION_CANCEL).appendPackageName(), PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val resumePendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            applicationContext, PENDING_INTENT_REQUEST_CODE,
            Intent(ACTION_RESUME).appendPackageName(), PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val buttonClickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_PAUSE -> timer.pause()
                ACTION_RESUME -> timer.resume()
                ACTION_CANCEL -> timer.cancel()
            }
        }
    }

    private fun createNotificationChannel() {
        NotificationChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            notificationManager?.createNotificationChannel(this)
        }
    }

    override fun onCreate() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
        registerButtonClickReceiver()
        super.onCreate()
    }

    override fun onDestroy() {
        unregisterButtonClickReceiver()
        timer.cancel()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            val durationMillis = intent?.getLongExtra(TIME_MILLIS_KEY, 0L) ?: 0
            timer.setAndStart(
                durationMillis = durationMillis
            )
        } catch (e: TimerAlreadyRunningException) {
            Toast.makeText(
                applicationContext,
                getText(R.string.error_timer_is_already_running),
                Toast.LENGTH_LONG
            ).show()
        }

        return START_STICKY
    }

    override fun onTick() {
        val notification = baseNotificationBuilder
            .addAction(0, getString(R.string.notification_button_cancel), cancelPendingIntent)
            .addAction(0, getString(R.string.notification_button_pause), pausePendingIntent)
            .setContentTitle(timer.millisLeft.toHhMmSs())
            .build()

        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    override fun onTimerPause() {
        val notification = baseNotificationBuilder
            .addAction(0, getString(R.string.notification_button_cancel), cancelPendingIntent)
            .addAction(0, getString(R.string.notification_button_resume), resumePendingIntent)
            .setContentTitle(timer.millisLeft.toHhMmSs())
            .build()

        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    override fun onTimerCancel() = stopSelf()

    override fun onTimeOver() {
        val notification = baseNotificationBuilder
            .setContentTitle((0L).toHhMmSs())
            .build()

        notificationManager?.notify(NOTIFICATION_ID, notification)

        playSound(applicationContext, R.raw.double_ping) {
            stopSelf()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun registerButtonClickReceiver() {
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
    }

    private fun unregisterButtonClickReceiver() {
        // FIXME: For some reasons, "unregisterReceiver" throws
        //  .IllegalArgumentException: Receiver not registered
        try {
            unregisterReceiver(buttonClickReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun Intent.appendPackageName(): Intent = apply {
        `package` = applicationContext.packageName
    }
}