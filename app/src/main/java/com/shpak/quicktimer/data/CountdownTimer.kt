package com.shpak.quicktimer.data

import java.util.Timer
import java.util.TimerTask

interface TimerListener {
    fun onTick()
    fun onTimeOver()
    fun onTimerPause()
    fun onTimerCancel()
}

class CountdownTimer(
    private val timerListener: TimerListener
) {
    companion object {
        private const val MILLIS_IN_SECOND = 1000L
    }

    var millisLeft = 0L
        private set

    private var timer: Timer? = null

    // Returns true, if start succeeds and false, if it doesn't
    // (in case if timer is already running)
    fun setAndStart(timeMillis: Long): Boolean {
        if (timer != null) return false

        if (timeMillis <= 0L) {
            timerListener.onTimeOver()
            return true
        }

        millisLeft = timeMillis

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                millisLeft -= MILLIS_IN_SECOND

                timerListener.onTick()

                if (millisLeft == 0L) {
                    cancelAndClear()
                    timerListener.onTimeOver()
                }
            }
        }, 0, 1000)

        return true
    }

    fun pause() {
        cancelAndClear()
        timerListener.onTimerPause()
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