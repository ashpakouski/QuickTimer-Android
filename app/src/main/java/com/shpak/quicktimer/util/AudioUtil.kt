package com.shpak.quicktimer.util

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RawRes

fun playSound(
    context: Context,
    @RawRes resourceId: Int,
    onComplete: (() -> Unit?)? = null
) = MediaPlayer.create(context, resourceId).run {
    setOnCompletionListener {
        reset()
        release()
        onComplete?.invoke()
    }

    start()
}

fun AudioManager.currentVolumeFraction(): Float {
    val volumeMin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        getStreamMinVolume(AudioManager.STREAM_MUSIC) else 0
    val volumeMax = getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    val volumeCurrent = getStreamVolume(AudioManager.STREAM_MUSIC)
    return (volumeCurrent - volumeMin) / (volumeMax - volumeMin).toFloat()
}