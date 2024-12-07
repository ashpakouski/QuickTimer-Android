package com.shpak.quicktimer.presentation

import android.content.Context
import android.view.LayoutInflater
import com.shpak.quicktimer.databinding.NotificationsPermissionRequestDialogBinding
import com.shpak.quicktimer.util.redirectToNotificationSettings

class NotificationPermissionRequestDialog(context: Context) : CustomDialog(context) {

    private val binding = NotificationsPermissionRequestDialogBinding.inflate(
        LayoutInflater.from(context), null, false
    )

    init {
        setContentView(binding.root)

        setOnShowListener { onShow() }
    }

    private fun onShow() {
        binding.buttonPositive.setOnClickListener {
            redirectToNotificationSettings(context.applicationContext)
            dismiss()
        }

        binding.buttonNegative.setOnClickListener {
            dismiss()
        }
    }
}