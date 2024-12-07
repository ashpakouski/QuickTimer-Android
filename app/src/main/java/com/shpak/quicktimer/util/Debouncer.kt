package com.shpak.quicktimer.util

class Debouncer(private val delayMillis: Long) {
    private var lastCallMillis = 0L

    fun call(block: () -> Unit) {
        val now = System.currentTimeMillis()

        if (now - lastCallMillis >= delayMillis) {
            lastCallMillis = now
            block()
        }
    }
}