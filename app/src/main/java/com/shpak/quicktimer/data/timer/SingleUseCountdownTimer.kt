package com.shpak.quicktimer.data.timer

import android.content.Context

interface PausableTimer : TimerToggle {
    fun pause()
    fun resume()
    val isRunning: Boolean
}

interface TimerListener {
    fun onTick()
    fun onTimeOver()
}

class SingleUseCountdownTimer(
    private val timerListener: TimerListener,
    context: Context
) : PausableTimer {

    private var startedAtMillis = 0L
    private var timerDurationMillis = 0L
    val millisLeft get() = timerDurationMillis - (System.currentTimeMillis() - startedAtMillis)

    private var isFinished = false

    override var isRunning: Boolean = false
        private set

    private val tickGenerator = TickGenerator(onTick = {
        if (millisLeft >= 0) timerListener.onTick()
        if (millisLeft <= 0) onTimeOver()
    })

    private val systemTimer = SystemTimer(context, onTimeOver = ::onTimeOver)

    override fun start(durationMillis: Long) {
        isRunning = true

        startedAtMillis = System.currentTimeMillis()
        timerDurationMillis = durationMillis

        tickGenerator.start()
        systemTimer.start(durationMillis)
    }

    override fun pause() {
        isRunning = false

        cancel()
        timerDurationMillis = millisLeft
    }

    override fun resume() {
        isRunning = true

        start(timerDurationMillis)
    }

    private fun onTimeOver() {
        if (isFinished) return

        isRunning = false
        isFinished = true

        tickGenerator.cancel()
        timerListener.onTimeOver()
    }

    override fun cancel() {
        tickGenerator.cancel()
        systemTimer.cancel()
    }
}