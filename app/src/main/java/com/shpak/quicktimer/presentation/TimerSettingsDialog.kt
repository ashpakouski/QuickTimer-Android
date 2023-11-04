package com.shpak.quicktimer.presentation

import android.app.AlertDialog
import android.content.Context
import android.service.quicksettings.TileService
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.NumberPicker
import com.shpak.quicktimer.R

object TimerSettingsDialog {
    fun show(tileService: TileService) {
        val context = tileService.applicationContext
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = layoutInflater.inflate(R.layout.timer_settings_layout, null)

        dialogView.findViewById<NumberPicker>(R.id.hours_picker).also {
            it.maxValue = 11
            it.minValue = 0
            it.value = 0
        }

        dialogView.findViewById<NumberPicker>(R.id.mins_picker).also {
            it.maxValue = 59
            it.minValue = 0
            it.value = 0
        }

        dialogView.findViewById<NumberPicker>(R.id.seconds_picker).also {
            it.maxValue = 59
            it.minValue = 0
            it.value = 0
        }

        val builder = AlertDialog.Builder(
            ContextThemeWrapper(
                tileService.applicationContext,
                R.style.TimerSettingsDialog
            )
        )

        builder.setTitle(context.getString(R.string.select_time_title))
            .setView(dialogView)
            .setPositiveButton(context.getString(R.string.button_start)) { _, _ ->
                TimerService.start(context)
            }
            .setNegativeButton(context.getString(R.string.button_cancel)) { _, _ -> }
        tileService.showDialog(builder.create())
    }
}