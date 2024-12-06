package com.shpak.quicktimer.data.timer

interface TimerToggle {
    fun start(durationMillis: Long)
    fun cancel()
}