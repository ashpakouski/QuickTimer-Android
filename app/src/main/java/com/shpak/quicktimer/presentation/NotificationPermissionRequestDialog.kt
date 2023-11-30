package com.shpak.quicktimer.presentation

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import com.shpak.quicktimer.R

object NotificationPermissionRequestDialog {
    fun build(
        context: Context,
        onRequestGranted: (() -> Unit)? = null,
        onRequestDenied: (() -> Unit)? = null
    ): Dialog {
        val dialogBuilder = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_launcher_foreground)
            .setMessage(
                context.getString(
                    R.string.notifications_permission_request_message,
                    context.getString(R.string.app_name)
                )
            )
            .setPositiveButton(context.getText(R.string.notification_permission_request_option_grant)) { dialog, _ ->
                dialog.dismiss()
                onRequestGranted?.invoke()
            }
            .setNegativeButton(R.string.notification_permission_request_option_deny) { dialog, _ ->
                dialog.dismiss()
                onRequestDenied?.invoke()
            }

        return dialogBuilder.create()
    }
}