package com.shpak.quicktimer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.quicksettings.TileService
import android.util.Log
import com.shpak.quicktimer.dialog.NotificationPermissionRequestDialog
import com.shpak.quicktimer.dialog.TimerSettingsDialog
import com.shpak.quicktimer.util.areNotificationsEnabled
import com.shpak.quicktimer.util.redirectToAppSettings
import java.util.Timer
import java.util.TimerTask

class TimerTileService : TileService() {

    private val configurationChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

        }
    }

    override fun onCreate() {
        super.onCreate()

        registerReceiver(
            configurationChangeReceiver,
            IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        )
    }

    override fun onClick() {
        super.onClick()

        if (areNotificationsEnabled(applicationContext)) {
            TimerSettingsDialog.show(this)
        } else {
            NotificationPermissionRequestDialog.show(
                this,
                onRequestGranted = {
                    redirectToAppSettings(applicationContext)
                })
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(configurationChangeReceiver)
    }
}