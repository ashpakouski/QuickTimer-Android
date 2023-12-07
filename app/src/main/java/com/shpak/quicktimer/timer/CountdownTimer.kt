package com.shpak.quicktimer.timer

import java.util.Timer
import java.util.TimerTask

// This is an encapsulation of timer-related logic
class CountdownTimer(
    private val timerListener: TimerListener
) {
    companion object {
        private const val MILLIS_IN_SECOND = 1000
    }

    private var millisLeft: Long = 0
    private var timer: Timer? = null

    fun setAndStart(timeMillis: Long) {
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

    }

    fun resume() {

    }

    fun reset() {
        timer?.cancel()
        timerListener.onTick(0)
    }
}