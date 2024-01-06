package com.shpak.quicktimer.presentation

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.shpak.quicktimer.databinding.NotificationsPermissionRequestDialogBinding

object NotificationPermissionRequestDialog {
    fun build(
        context: Context,
        onRequestGranted: (() -> Unit)? = null,
        onRequestDenied: (() -> Unit)? = null
    ): Dialog {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding =
            NotificationsPermissionRequestDialogBinding.inflate(layoutInflater, null, false)

        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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