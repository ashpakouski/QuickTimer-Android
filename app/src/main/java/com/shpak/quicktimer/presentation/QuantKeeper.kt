package com.shpak.quicktimer.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.shpak.quicktimer.R
import com.shpak.quicktimer.util.lazyTryOrNull

class QuantKeeper : Service() {

    companion object {
        private const val NOTIFICATION_ID = 5

        fun start(context: Context) {
            val startIntent = Intent(context, QuantKeeper::class.java)
            context.startForegroundService(startIntent)
        }

        fun stop(context: Context) {
            val stopIntent = Intent(context, QuantKeeper::class.java)
            context.stopService(stopIntent)
        }
    }

    private val notificationManager by lazyTryOrNull {
        getSystemService(NotificationManager::class.java)
    }

    private val baseNotificationBuilder: NotificationCompat.Builder
        get() = NotificationCompat
            .Builder(applicationContext, getString(R.string.notification_channel_id))
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_timer)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.setContentTitle("Keeper").build())
        //notificationManager?.notify(NOTIFICATION_ID, baseNotificationBuilder.setContentTitle("Keeper").build())
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
}