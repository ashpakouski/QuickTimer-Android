package com.shpak.quicktimer.data

interface TimerListener {
    fun onTick(leftTimeMillis: Long)
    fun onTimeOver()
    fun onTimerPause(leftTimeMillis: Long)
    fun onTimerCancel()
}