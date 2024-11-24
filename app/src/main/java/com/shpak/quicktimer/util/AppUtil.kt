package com.shpak.quicktimer.util

import android.content.Context
import android.content.Intent
import android.provider.Settings

fun redirectToNotificationSettings(context: Context) =
    with(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)) {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)

        context.startActivity(this)
    }