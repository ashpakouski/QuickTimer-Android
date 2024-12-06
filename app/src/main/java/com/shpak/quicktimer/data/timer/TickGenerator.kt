package com.shpak.quicktimer.data.timer

import java.util.Timer
import java.util.TimerTask

class TickGenerator(
    val onTick: () -> Unit
) {
    companion object {
        private const val MILLIS_IN_SECOND = 1000L
    }

    private var timer: Timer? = null

    fun start() {
        registerTickTimer()
    }

    fun cancel() {
        timer?.cancel()
        timer = null
    }

    private fun registerTickTimer() {
        cancel()

        timer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() = onTick()
            }, 0, MILLIS_IN_SECOND)
        }
    }
}