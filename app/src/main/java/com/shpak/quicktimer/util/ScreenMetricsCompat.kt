package com.shpak.quicktimer.util

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowManager
import androidx.annotation.RequiresApi

object ScreenMetricsCompat {

    fun getScreenSize(context: Context): Size = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        getScreenSizeOld(context)
    } else {
        getScreenSizeApi30(context)
    }

    private fun getScreenSizeOld(context: Context): Size {
        val display = context.getSystemService(WindowManager::class.java).defaultDisplay
        val metrics = if (display != null) {
            DisplayMetrics().also { display.getRealMetrics(it) }
        } else {
            Resources.getSystem().displayMetrics
        }
        return Size(metrics.widthPixels, metrics.heightPixels)
    }


    @RequiresApi(30)
    private fun getScreenSizeApi30(context: Context): Size {
        val metrics = context.getSystemService(WindowManager::class.java).currentWindowMetrics
        return Size(metrics.bounds.width(), metrics.bounds.height())
    }
}