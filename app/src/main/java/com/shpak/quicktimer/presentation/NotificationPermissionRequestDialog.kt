package com.shpak.quicktimer.presentation

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.shpak.quicktimer.databinding.NotificationsPermissionRequestDialogBinding

object NotificationPermissionRequestDialog {
    fun build(
        context: Context,
        onRequestGranted: (() -> Unit)? = null,
        onRequestDenied: (() -> Unit)? = null
    ): Dialog {
        val binding = NotificationsPermissionRequestDialogBinding.inflate(
            LayoutInflater.from(context), null, false
        )
        val dialog = CustomDialog(binding.root)

        binding.buttonPositive.setOnClickListener {
            dialog.dismiss()
            onRequestGranted?.invoke()
        }

        binding.buttonNegative.setOnClickListener {
            dialog.dismiss()
            onRequestDenied?.invoke()
        }

        return dialog
    }
}