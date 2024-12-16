package com.shpak.quicktimer.presentation

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.shpak.quicktimer.R
import com.shpak.quicktimer.util.lazyTryOrNull

class QuantKeeper : Service() {

    companion object {
        private const val NOTIFICATION_ID = 5

        fun start(context: Context) {
            val startIntent = Intent(context, QuantKeeper::class.java)

            try {
                context.startForegroundService(startIntent)
            } catch (_: Exception) {
            }
        }

        fun stop(context: Context) {
            val stopIntent = Intent(context, QuantKeeper::class.java)

            try {
                context.stopService(stopIntent)
            } catch (_: Exception) {
            }
        }
    }

    private val notificationController by lazyTryOrNull {
        NotificationController(applicationContext)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        notificationController?.getNotification(
            getString(R.string.timer_settings_keeper_message)
        )?.let {
            startForeground(NOTIFICATION_ID, it)
        }
    }
}