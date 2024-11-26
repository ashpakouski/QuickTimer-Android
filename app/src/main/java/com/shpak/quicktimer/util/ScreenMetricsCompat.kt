package com.shpak.quicktimer.util

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowManager
import androidx.annotation.RequiresApi

private interface ScreenSizeProvider {
    fun getScreenSize(context: Context): Size
}

object ScreenMetricsCompat {

    private val screenSizeProvider: ScreenSizeProvider = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.R -> ScreenSizeProviderPreApi30()
        else -> ScreenSizeProviderApi30()
    }

    fun getScreenSize(context: Context): Size {
        return screenSizeProvider.getScreenSize(context)
    }
}

@Suppress("Deprecation")
private class ScreenSizeProviderPreApi30 : ScreenSizeProvider {

    override fun getScreenSize(context: Context): Size {
        val display = context.getSystemService(WindowManager::class.java)?.defaultDisplay
        val metrics = if (display != null) {
            DisplayMetrics().also { display.getRealMetrics(it) }
        } else {
            Resources.getSystem().displayMetrics
        }
        return Size(metrics.widthPixels, metrics.heightPixels)
    }
}

private class ScreenSizeProviderApi30 : ScreenSizeProvider {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun getScreenSize(context: Context): Size {
        val metrics = context.getSystemService(WindowManager::class.java)?.currentWindowMetrics
        return Size(metrics?.bounds?.width() ?: 0, metrics?.bounds?.height() ?: 0)
    }
}