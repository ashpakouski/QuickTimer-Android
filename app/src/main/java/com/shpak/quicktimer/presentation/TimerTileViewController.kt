package com.shpak.quicktimer.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.shpak.quicktimer.util.areNotificationsEnabled
import com.shpak.quicktimer.util.redirectToAppSettings

// It behaves more like a View than like a Service, so let's give this naming a try
class TimerTileViewController : TileService() {

    private val configurationChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO: Redraw dialog, if theme changes
        }
    }

    override fun onCreate() {
        super.onCreate()

        registerReceiver(
            configurationChangeReceiver,
            IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        )
    }

    override fun onStartListening() {
        super.onStartListening()
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()

        if (areNotificationsEnabled(applicationContext)) {
            showTimerSettingsDialog()
        } else {
            requestNotificationsPermission()
        }
    }

    private fun showTimerSettingsDialog() {
        showDialog(
            TimerSettingsDialog.build(
                applicationContext,
                onTimerSet = { timeMillis ->
                    TimerService.start(applicationContext, timeMillis)
                }
            )
        )
    }

    private fun requestNotificationsPermission() {
        NotificationPermissionRequestDialog.build(
            applicationContext,
            onRequestGranted = {
                redirectToAppSettings(applicationContext)
            }
        ).apply {
            showDialog(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(configurationChangeReceiver)
    }
}