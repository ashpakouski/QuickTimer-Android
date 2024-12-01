package com.shpak.quicktimer.data

import java.util.Timer
import java.util.TimerTask

class TimerAlreadyRunningException : Exception("Timer is already running")

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

    private var startedAtMillis = 0L
    private var timerDurationMillis = 0L
    val millisLeft: Long
        get() = timerDurationMillis - (System.currentTimeMillis() - startedAtMillis)

    private var timer: Timer? = null

    fun setAndStart(durationMillis: Long) {
        if (timer != null) throw TimerAlreadyRunningException()

        timerDurationMillis = durationMillis
        startedAtMillis = System.currentTimeMillis()

        if (durationMillis <= 0L) {
            onTimeOver()
            return
        }

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                timerListener.onTick()

                if (millisLeft <= 0L) {
                    onTimeOver()
                }
            }
        }, 0, MILLIS_IN_SECOND)
    }

    fun pause() {
        cancelAndClear()
        timerListener.onTimerPause()
        timerDurationMillis = millisLeft
    }

    fun resume() {
        setAndStart(timerDurationMillis)
    }

    fun cancel() {
        cancelAndClear()
        timerListener.onTimerCancel()
    }

    private fun onTimeOver() {
        cancelAndClear()
        timerListener.onTimeOver()
    }

    private fun cancelAndClear() {
        timer?.cancel()
        timer = null
    }
}