package com.shpak.quicktimer.data

import android.content.Context
import android.content.DialogInterface
import android.database.ContentObserver
import android.media.AudioManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.KeyEvent
import com.shpak.quicktimer.util.currentVolumeFraction
import com.shpak.quicktimer.util.volumeStepFraction

class MediaVolumeTracker(
    private val context: Context,
    private val onVolumeFractionChange: (Float) -> Unit
) : DialogInterface.OnKeyListener {

    private val audioManager = context.getSystemService(AudioManager::class.java)

    private val systemVolumeChangeListener = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)

            if (uri?.lastPathSegment == "volume_music_speaker") {
                audioManager?.currentVolumeFraction()?.let(onVolumeFractionChange)
            }
        }
    }

    val currentVolume: Float? get() = audioManager?.currentVolumeFraction()

    fun start() {
        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI, true, systemVolumeChangeListener
        )
    }

    fun stop() {
        context.contentResolver.unregisterContentObserver(systemVolumeChangeListener)
    }

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        val audioManager = audioManager ?: return false

        when (event?.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> onVolumeFractionChange(
                (audioManager.currentVolumeFraction() + audioManager.volumeStepFraction()).coerceAtMost(1.0f)
            )

            KeyEvent.KEYCODE_VOLUME_DOWN -> onVolumeFractionChange(
                (audioManager.currentVolumeFraction() - audioManager.volumeStepFraction()).coerceAtLeast(0.0f)
            )
        }

        return false
    }
}