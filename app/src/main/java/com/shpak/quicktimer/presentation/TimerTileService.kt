package com.shpak.quicktimer.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.quicksettings.TileService
import com.shpak.quicktimer.util.areNotificationsEnabled
import com.shpak.quicktimer.util.redirectToAppSettings

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