package com.shpak.quicktimer.util

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes

fun playSound(
    context: Context,
    @RawRes resourceId: Int,
    onCompletionListener: (() -> Unit?)? = null
) {
    val mediaPlayer = MediaPlayer.create(context, resourceId)
    mediaPlayer.setOnCompletionListener {
        mediaPlayer.reset()
        mediaPlayer.release()
        onCompletionListener?.invoke()
    }
    mediaPlayer.start()
}