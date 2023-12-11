package com.shpak.quicktimer.timer

import java.util.Timer
import java.util.TimerTask

class CountdownTimer(
    private val timerListener: TimerListener
) {
    companion object {
        private const val MILLIS_IN_SECOND = 1000L
    }

    var millisLeft = 0L
    private var timer: Timer? = null

    fun setAndStart(timeMillis: Long) {
        if (timeMillis <= 0L) {
            timerListener.onTimeOver()
            return
        }

        millisLeft = timeMillis

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                timerListener.onTick(millisLeft)
                millisLeft -= MILLIS_IN_SECOND

                if (millisLeft == 0L) {
                    timer?.cancel()
                    timerListener.onTimeOver()
                }
            }
        }, 0, 1000)
    }

    fun pause() {
        timer?.cancel()
    }

    fun resume() {
        setAndStart(millisLeft)
    }

    fun cancel() {
        timer?.cancel()
    }
}