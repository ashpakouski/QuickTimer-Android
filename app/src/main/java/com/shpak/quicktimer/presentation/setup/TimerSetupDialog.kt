package com.shpak.quicktimer.presentation.setup

import android.app.Dialog
import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import com.shpak.quicktimer.databinding.TimerSettingsDialogBinding
import com.shpak.quicktimer.presentation.CustomDialog
import com.shpak.quicktimer.util.HapticsCompat
import com.shpak.quicktimer.util.currentVolumeFraction

object TimerSetupDialog {

    fun build(
        context: Context,
        onTimerSet: ((timeMillis: Long) -> Unit)? = null,
        onCancel: (() -> Unit)? = null
    ): Dialog {
        val haptics = HapticsCompat(context.applicationContext)
        val binding = TimerSettingsDialogBinding.inflate(LayoutInflater.from(context), null, false)

        // TODO
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

        binding.warningView.visibility = if (
            audioManager != null && audioManager.currentVolumeFraction() <= 0.3f
        ) View.VISIBLE else View.GONE

        context.contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI,
            true,
            object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    super.onChange(selfChange, uri)

                    if (uri?.lastPathSegment == "volume_music_speaker") {
                        binding.warningView.visibility = if (
                            audioManager != null && audioManager.currentVolumeFraction() <= 0.3f
                        ) View.VISIBLE else View.GONE
                    }
                }
            })

        val dialog = CustomDialog(binding.root)

        dialog.setOnShowListener {
            setupPickers(binding) { _, _, _ ->
                haptics.generateSingleTick()
                binding.buttonPositive.isEnabled = collectTime(binding) != 0L
            }
        }

        binding.buttonPositive.setOnClickListener {
            onTimerSet?.invoke(
                collectTime(binding)
            )
            dialog.dismiss()
        }

        binding.buttonNegative.setOnClickListener {
            onCancel?.invoke()
            dialog.dismiss()
        }

        return dialog
    }

    private fun setupPickers(
        binding: TimerSettingsDialogBinding,
        onValueChangedListener: NumberPicker.OnValueChangeListener
    ) {
        binding.hoursPicker.apply {
            maxValue = 11
            minValue = 0
            value = 0
            setOnValueChangedListener(onValueChangedListener)
        }

        binding.minutesPicker.apply {
            maxValue = 59
            minValue = 0
            value = 0
            setOnValueChangedListener(onValueChangedListener)
        }

        binding.secondsPicker.apply {
            maxValue = 59
            minValue = 0
            value = 0
            setOnValueChangedListener(onValueChangedListener)
        }
    }

    private fun collectTime(binding: TimerSettingsDialogBinding): Long {
        val hours = binding.hoursPicker.value
        val minutes = binding.minutesPicker.value
        val seconds = binding.secondsPicker.value

        return (hours * 60 * 60 + minutes * 60 + seconds) * 1000L
    }
}