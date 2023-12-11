package com.shpak.quicktimer.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Long.toHhMmSs(): String {
    return SimpleDateFormat("HH:mm:ss", Locale.US).let {
        it.timeZone = TimeZone.getTimeZone("GMT+0")
        it.format(Date(this))
    }
}