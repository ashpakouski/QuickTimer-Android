package com.shpak.quicktimer.presentation

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.NumberPicker
import com.shpak.quicktimer.databinding.TimerSettingsDialogBinding

object TimerSettingsDialog {

    fun build(
        context: Context,
        onTimerSet: ((timeMillis: Long) -> Unit)? = null,
        onCancel: (() -> Unit)? = null
    ): Dialog {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = TimerSettingsDialogBinding.inflate(layoutInflater, null, false)

        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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