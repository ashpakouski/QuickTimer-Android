package com.shpak.quicktimer.dialog

import android.app.AlertDialog
import android.service.quicksettings.TileService
import com.shpak.quicktimer.R

object NotificationPermissionRequestDialog {
    fun show(
        tileService: TileService,
        onRequestGranted: (() -> Unit)? = null,
        onRequestDenied: (() -> Unit)? = null
    ) {
        val dialog = AlertDialog.Builder(tileService)
            .setIcon(R.drawable.ic_launcher_foreground)
            .setMessage(
                tileService.getString(
                    R.string.notifications_permission_request_message,
                    tileService.getString(R.string.app_name)
                )
            )
            .setPositiveButton(tileService.getText(R.string.notification_permission_request_option_grant)) { _, _ ->
                onRequestGranted?.invoke()
            }
            .setNegativeButton(R.string.notification_permission_request_option_deny) { dialog, _ ->
                dialog.dismiss()
                onRequestDenied?.invoke()
            }
            .create()
        tileService.showDialog(dialog)
    }
}