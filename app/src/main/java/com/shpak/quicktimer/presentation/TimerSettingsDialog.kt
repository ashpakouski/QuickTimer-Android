package com.shpak.quicktimer.presentation

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.NumberPicker
import com.shpak.quicktimer.R
import com.shpak.quicktimer.databinding.TimerSettingsLayoutBinding

object TimerSettingsDialog {
    fun build(
        context: Context,
        onTimerSet: ((timeMillis: Long) -> Unit)? = null,
        onCancel: (() -> Unit)? = null
    ): Dialog {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = TimerSettingsLayoutBinding.inflate(layoutInflater, null, false)

        setupPickers(binding.hoursPicker, binding.minsPicker, binding.secondsPicker)

        val builder = AlertDialog.Builder(
            ContextThemeWrapper(
                context,
                R.style.QuickTimerAppDialog
            )
        )

        builder.setTitle(context.getString(R.string.select_time_title))
            .setView(binding.root)
            .setPositiveButton(context.getString(R.string.button_start)) { dialog, _ ->
                onTimerSet?.invoke(
                    collectTime(binding.hoursPicker, binding.minsPicker, binding.secondsPicker)
                )
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.button_cancel)) { dialog, _ ->
                onCancel?.invoke()
                dialog.dismiss()
            }

        return builder.create()
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