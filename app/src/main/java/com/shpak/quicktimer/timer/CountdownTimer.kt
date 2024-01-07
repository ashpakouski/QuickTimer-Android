package com.shpak.quicktimer.timer

import java.util.Timer
import java.util.TimerTask

class CountdownTimer(
    private val timerListener: TimerListener
) {
    companion object {
        private const val MILLIS_IN_SECOND = 1000L
    }

    private var millisLeft = 0L
    private var timer: Timer? = null

    fun setAndStart(timeMillis: Long) {
        if (timer != null) return

        if (timeMillis <= 0L) {
            timerListener.onTimeOver()
            return
        }

        millisLeft = timeMillis

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                millisLeft -= MILLIS_IN_SECOND

                timerListener.onTick(millisLeft)

                if (millisLeft == 0L) {
                    cancelAndClear()
                    timerListener.onTimeOver()
                }
            }
        }, 0, 1000)
    }

    fun pause() {
        cancelAndClear()
        timerListener.onTimerPause(millisLeft)
    }

    fun resume() {
        setAndStart(millisLeft)
    }

    fun cancel() {
        cancelAndClear()
        timerListener.onTimerCancel()
    }

    private fun cancelAndClear() {
        timer?.cancel()
        timer = null
    }
}