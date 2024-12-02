package com.shpak.quicktimer.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.shpak.quicktimer.util.lazyTryOrNull
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
    private val timerListener: TimerListener,
    private val context: Context
) {
    companion object {
        private const val MILLIS_IN_SECOND = 1000L
        private const val INTENT_ACTION_ALARM = "action_alarm"
        private const val PENDING_INTENT_REQUEST_CODE = 3
    }

    private val alarmManager by lazyTryOrNull {
        context.getSystemService(AlarmManager::class.java)
    }

    private val alarmPendingIntent by lazyTryOrNull {
        PendingIntent.getBroadcast(
            context, PENDING_INTENT_REQUEST_CODE,
            Intent(INTENT_ACTION_ALARM).appendPackageName(context), PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val alarmReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                INTENT_ACTION_ALARM -> onTimeOver()
            }
        }
    }

    private var startedAtMillis = 0L
    private var timerDurationMillis = 0L
    val millisLeft get() = timerDurationMillis - (System.currentTimeMillis() - startedAtMillis)

    private var timer: Timer? = null

    fun setAndStart(durationMillis: Long) {
        if (timer != null) throw TimerAlreadyRunningException()

        timerDurationMillis = durationMillis
        startedAtMillis = System.currentTimeMillis()

        if (durationMillis <= 0L) {
            onTimeOver()
            return
        }

        registerSystemAlarm(durationMillis)
        registerTickTimer()
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

    private fun registerSystemAlarm(durationMillis: Long) {
        registerAlarmReceiver()

        alarmPendingIntent?.let { pendingIntent ->
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + durationMillis,
                pendingIntent
            )
        }
    }

    private fun registerTickTimer() {
        timer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    timerListener.onTick()

                    if (millisLeft <= 0L) {
                        onTimeOver()
                    }
                }
            }, 0, MILLIS_IN_SECOND)
        }
    }

    private fun onTimeOver() {
        cancelAndClear()
        timerListener.onTimeOver()
    }

    private fun cancelAndClear() {
        unregisterAlarmReceiver()

        alarmPendingIntent?.let { pendingIntent ->
            alarmManager?.cancel(pendingIntent)
        }

        timer?.cancel()
        timer = null
    }

    private fun registerAlarmReceiver() {
        ContextCompat.registerReceiver(
            context,
            alarmReceiver,
            IntentFilter().apply {
                addAction(INTENT_ACTION_ALARM)
            },
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun unregisterAlarmReceiver() {
        try {
            context.unregisterReceiver(alarmReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}

private fun Intent.appendPackageName(context: Context): Intent = apply {
    `package` = context.packageName
}