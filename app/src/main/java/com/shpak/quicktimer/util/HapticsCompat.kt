package com.shpak.quicktimer.util

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi

private interface HapticsProvider {
    fun generateSingleTick()
}

class HapticsCompat(context: Context) {

    private val hapticsProvider: HapticsProvider = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> HapticsProviderPreApi31(context)
        else -> HapticsProviderApi31(context)
    }

    fun generateSingleTick() {
        hapticsProvider.generateSingleTick()
    }
}

private class HapticsProviderPreApi31(context: Context) : HapticsProvider {
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    override fun generateSingleTick() {
        vibrator?.generateSingleTick()
    }
}

@RequiresApi(Build.VERSION_CODES.S)
private class HapticsProviderApi31(context: Context) : HapticsProvider {

    private val vibratorManager =
        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager

    override fun generateSingleTick() {
        val effect = VibrationEffect.Composition.PRIMITIVE_LOW_TICK

        if (isPrimitiveSupported(effect)) {
            vibratorManager?.vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.startComposition().addPrimitive(effect).compose()
                ),
                VibrationAttributes.Builder()
                    .setUsage(VibrationAttributes.USAGE_ALARM)
                    .build()
            )
        } else {
            vibratorManager?.defaultVibrator?.generateSingleTick()
        }
    }

    private fun isPrimitiveSupported(primitiveEffectId: Int) =
        vibratorManager?.defaultVibrator?.areAllPrimitivesSupported(primitiveEffectId) == true
}

private fun Vibrator.generateSingleTick() {
    vibrate(
        VibrationEffect.createOneShot(10, 155),
        AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
            .setUsage(AudioAttributes.USAGE_ASSISTANT)
            .build()
    )
}