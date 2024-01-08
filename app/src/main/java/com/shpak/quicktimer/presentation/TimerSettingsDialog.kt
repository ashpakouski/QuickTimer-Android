package com.shpak.quicktimer.presentation

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.NumberPicker
import com.shpak.quicktimer.databinding.TimerSettingsDialogBinding

object TimerSettingsDialog {

    fun build(
        context: Context,
        onTimerSet: ((timeMillis: Long) -> Unit)? = null,
        onCancel: (() -> Unit)? = null
    ): Dialog {
        val binding = TimerSettingsDialogBinding.inflate(LayoutInflater.from(context), null, false)
        val dialog = CustomDialog(binding.root)

        dialog.setOnShowListener {
            setupPickers(binding.hoursPicker, binding.minsPicker, binding.secondsPicker)
        }

        binding.buttonPositive.setOnClickListener {
            onTimerSet?.invoke(
                collectTime(binding.hoursPicker, binding.minsPicker, binding.secondsPicker)
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
        hoursPicker: NumberPicker,
        minutesPicker: NumberPicker,
        secondsPicker: NumberPicker
    ) {
        hoursPicker.apply {
            maxValue = 11
            minValue = 0
            value = 0
        }

        minutesPicker.apply {
            maxValue = 59
            minValue = 0
            value = 0
        }

        secondsPicker.apply {
            maxValue = 59
            minValue = 0
            value = 0
        }
    }

    private fun collectTime(
        hoursPicker: NumberPicker,
        minutesPicker: NumberPicker,
        secondsPicker: NumberPicker
    ): Long {
        val hours = hoursPicker.value
        val minutes = minutesPicker.value
        val seconds = secondsPicker.value

        return (hours * 60 * 60 + minutes * 60 + seconds) * 1000L
    }
}