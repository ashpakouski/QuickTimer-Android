package com.shpak.quicktimer.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Long.toHhMmSs(): String =
    with(SimpleDateFormat("HH:mm:ss", Locale.US)) {
        timeZone = TimeZone.getTimeZone("GMT+0")
        format(Date(this@toHhMmSs))
    }