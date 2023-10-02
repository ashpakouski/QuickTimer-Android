package com.shpak.quicktimer.service

import android.service.quicksettings.TileService
import com.shpak.quicktimer.dialog.NotificationPermissionRequestDialog
import com.shpak.quicktimer.dialog.TimerSettingsDialog
import com.shpak.quicktimer.util.areNotificationsEnabled
import com.shpak.quicktimer.util.redirectToAppSettings

class TimerTileService : TileService() {
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
}