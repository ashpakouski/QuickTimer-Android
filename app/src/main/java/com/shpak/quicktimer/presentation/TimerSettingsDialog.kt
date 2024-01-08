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
            setupPickers(binding) { _, _, _ ->
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