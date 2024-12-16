package com.shpak.quicktimer.presentation

import android.app.Dialog
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.shpak.quicktimer.R
import com.shpak.quicktimer.util.areNotificationsEnabled

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

        try {
            if (areNotificationsEnabled(applicationContext)) {
                showTimerSettingsDialog()
            } else {
                requestNotificationsPermission()
            }
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext, R.string.error_cant_show_dialog, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showTimerSettingsDialog() {
        if (timerSettingsDialog?.isShowing == true) return

        timerSettingsDialog = TimerSetupDialog(applicationContext).apply {
            showDialog(this)
        }
    }

    private fun requestNotificationsPermission() {
        if (permissionRequestDialog?.isShowing == true) return

        permissionRequestDialog = NotificationPermissionRequestDialog(applicationContext).apply {
            showDialog(this)
        }
    }
}