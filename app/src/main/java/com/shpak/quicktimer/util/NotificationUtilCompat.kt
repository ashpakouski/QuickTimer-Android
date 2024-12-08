package com.shpak.quicktimer.util

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

fun areNotificationsEnabled(context: Context): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        context.getSystemService(NotificationManager::class.java)
            ?.areNotificationsEnabled() == true
    }

fun getNotificationButton(
    context: Context, action: String, @StringRes stringId: Int, requestCode: Int = 42
): NotificationCompat.Action = getNotificationButton(
    context, action, context.getString(stringId), requestCode
)

fun getNotificationButton(
    context: Context, action: String, title: String, requestCode: Int = 42
): NotificationCompat.Action {
    val cancelPendingIntent = PendingIntent.getBroadcast(
        context, requestCode,
        Intent(action).appendPackageName(context), PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Action(0, title, cancelPendingIntent)
}

private fun Intent.appendPackageName(context: Context): Intent = apply {
    `package` = context.packageName
}