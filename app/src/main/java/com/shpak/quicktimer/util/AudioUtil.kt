package com.shpak.quicktimer.util

import android.content.Context
import android.media.MediaPlayer
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