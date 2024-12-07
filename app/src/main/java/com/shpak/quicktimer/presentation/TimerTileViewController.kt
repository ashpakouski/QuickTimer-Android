package com.shpak.quicktimer.presentation

import android.app.Dialog
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.shpak.quicktimer.presentation.setup.TimerSetupDialog
import com.shpak.quicktimer.util.areNotificationsEnabled
import com.shpak.quicktimer.util.redirectToNotificationSettings

class TimerTileViewController : TileService() {

    private var permissionRequestDialog: Dialog? = null
    private var timerSettingsDialog: Dialog? = null

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
        if (timerSettingsDialog?.isShowing == true) return

        timerSettingsDialog = TimerSetupDialog(this).apply {
            showDialog(this)
        }
    }

    private fun requestNotificationsPermission() {
        if (permissionRequestDialog?.isShowing == true) return

        permissionRequestDialog = NotificationPermissionRequestDialog.build(
            applicationContext,
            onRequestGranted = {
                redirectToNotificationSettings(applicationContext)
            }
        ).apply {
            showDialog(this)
        }
    }
}