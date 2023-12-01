package com.shpak.quicktimer.presentation

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.NumberPicker
import com.shpak.quicktimer.R

object TimerSettingsDialog {
    fun build(
        context: Context,
        onTimerSet: ((timeMillis: Long) -> Unit)? = null,
        onCancel: (() -> Unit)? = null
    ): Dialog {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = layoutInflater.inflate(R.layout.timer_settings_layout, null)

        val hoursPicker = dialogView.findViewById<NumberPicker>(R.id.hours_picker)
        val minutesPicker = dialogView.findViewById<NumberPicker>(R.id.mins_picker)
        val secondsPicker = dialogView.findViewById<NumberPicker>(R.id.seconds_picker)

        setupPickers(hoursPicker, minutesPicker, secondsPicker)

        val builder = AlertDialog.Builder(
            ContextThemeWrapper(
                context,
                R.style.TimerSettingsDialog
            )
        )

        builder.setTitle(context.getString(R.string.select_time_title))
            .setView(dialogView)
            .setPositiveButton(context.getString(R.string.button_start)) { dialog, _ ->
                onTimerSet?.invoke(collectTime(hoursPicker, minutesPicker, secondsPicker))
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