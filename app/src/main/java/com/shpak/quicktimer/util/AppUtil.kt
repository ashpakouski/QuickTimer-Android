package com.shpak.quicktimer.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun redirectToAppSettings(context: Context) =
    with(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)) {
        data = Uri.fromParts("package", context.packageName, null)
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

        context.startActivity(this)
    }