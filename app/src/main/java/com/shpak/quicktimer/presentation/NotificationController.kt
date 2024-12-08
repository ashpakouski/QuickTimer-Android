package com.shpak.quicktimer.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.shpak.quicktimer.R

class NotificationController(private val context: Context) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    private val baseNotificationBuilder: NotificationCompat.Builder
        get() = NotificationCompat
            .Builder(context, context.getString(R.string.notification_channel_id))
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_timer)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)

    init {
        createNotificationChannel()
    }

    fun getNotification(
        message: String,
        actions: List<NotificationCompat.Action> = emptyList()
    ): Notification = baseNotificationBuilder.setContentTitle(message).apply {
        actions.forEach(::addAction)
    }.build()

    fun postNotification(
        notificationId: Int,
        message: String,
        actions: List<NotificationCompat.Action> = emptyList()
    ) {
        notificationManager.notify(notificationId, getNotification(message, actions))
    }

    private fun createNotificationChannel() {
        NotificationChannel(
            context.getString(R.string.notification_channel_id),
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            notificationManager?.createNotificationChannel(this)
        }
    }
}