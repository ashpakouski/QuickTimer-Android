package com.shpak.stopwatch.dialog

import android.app.AlertDialog
import android.service.quicksettings.TileService

object NotificationPermissionRequestDialog {
    fun show(tileService: TileService) {
        val builder = AlertDialog.Builder(tileService)
        builder.setTitle("Attention")
            .setMessage("Notifications permission is required for Quick Stopwatch to work")
            .setPositiveButton("Grant in settings") { _, _ -> }
            .setNegativeButton("Deny") { _, _ -> }
        tileService.showDialog(builder.create())
    }
}