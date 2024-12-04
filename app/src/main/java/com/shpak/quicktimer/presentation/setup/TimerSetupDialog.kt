package com.shpak.quicktimer.presentation.setup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.shpak.quicktimer.data.MediaVolumeTracker
import com.shpak.quicktimer.databinding.TimerSettingsDialogBinding
import com.shpak.quicktimer.presentation.CustomDialog
import com.shpak.quicktimer.presentation.QuantKeeper
import com.shpak.quicktimer.presentation.TimerService
import com.shpak.quicktimer.util.HapticsCompat

class TimerSetupDialog(context: Context) : CustomDialog(context) {

    companion object {
        private const val MIN_AUDIBLE_VOLUME_FRACTION = 0.35f
    }

    private val volumeTracker = MediaVolumeTracker(context, ::onVolumeFractionChange)
    private val haptics = HapticsCompat(context)

    private val binding = TimerSettingsDialogBinding.inflate(
        LayoutInflater.from(context), null, false
    )

    private val currentSelectionMillis: Long
        get() {
            val hours = binding.hoursPicker.value
            val minutes = binding.minutesPicker.value
            val seconds = binding.secondsPicker.value

            return (hours * 60 * 60 + minutes * 60 + seconds) * 1000L
        }

    init {
        setContentView(binding.root)

        setOnShowListener { onShow() }
        setOnDismissListener { onDismiss() }
        setOnKeyListener(volumeTracker)
    }

    private fun onShow() {
        QuantKeeper.start(context)

        setupPickers(::onPickerStateChange)

        binding.buttonPositive.setOnClickListener { onButtonPositiveClick() }
        binding.buttonNegative.setOnClickListener { onButtonNegativeClick() }

        volumeTracker.start()
        volumeTracker.currentVolume?.let(::onVolumeFractionChange)
    }

    private fun onDismiss() {
        QuantKeeper.stop(context)

        volumeTracker.stop()
    }

    private fun onButtonPositiveClick() {
        TimerService.start(context, currentSelectionMillis)
        dismiss()
    }

    private fun onButtonNegativeClick() {
        dismiss()
    }

    private fun onPickerStateChange() {
        haptics.generateSingleTick()
        binding.buttonPositive.isEnabled = currentSelectionMillis != 0L
    }

    private fun onVolumeFractionChange(volumeFraction: Float) {
        binding.warningView.visibility =
            if (volumeFraction < MIN_AUDIBLE_VOLUME_FRACTION) View.VISIBLE else View.GONE
    }

    private fun setupPickers(onPickerStateChange: () -> Unit) {
        binding.hoursPicker.apply {
            maxValue = 11
            minValue = 0
            value = 0
            setOnValueChangedListener { _, _, _ -> onPickerStateChange() }
        }

        binding.minutesPicker.apply {
            maxValue = 59
            minValue = 0
            value = 0
            setOnValueChangedListener { _, _, _ -> onPickerStateChange() }
        }

        binding.secondsPicker.apply {
            maxValue = 59
            minValue = 0
            value = 0
            setOnValueChangedListener { _, _, _ -> onPickerStateChange() }
        }
    }
}