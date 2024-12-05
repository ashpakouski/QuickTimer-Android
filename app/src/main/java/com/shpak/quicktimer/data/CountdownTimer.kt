package com.shpak.quicktimer.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import java.util.Timer
import java.util.TimerTask

class TimerAlreadyRunningException : Exception("Timer is already running")

interface TimerToggle {
    fun start(durationMillis: Long)
    fun cancel()
}

class SystemTimer(
    private val context: Context,
    private val onTimeOver: () -> Unit
) : TimerToggle {
    companion object {
        private const val INTENT_ACTION_ALARM = "action_alarm"
        private const val PENDING_INTENT_REQUEST_CODE = 3
    }

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    private val alarmPendingIntent = PendingIntent.getBroadcast(
        context, PENDING_INTENT_REQUEST_CODE,
        Intent(INTENT_ACTION_ALARM).appendPackageName(context), PendingIntent.FLAG_IMMUTABLE
    )

    private val alarmReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                INTENT_ACTION_ALARM -> onAlarm()
            }
        }
    }

    override fun start(durationMillis: Long) {
        registerSystemAlarm(durationMillis)
    }

    override fun cancel() {
        unregisterSystemAlarm()
    }

    private fun onAlarm() {
        unregisterSystemAlarm()
        onTimeOver()
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

    private fun unregisterSystemAlarm() {
        unregisterAlarmReceiver()

        alarmPendingIntent?.let { pendingIntent ->
            alarmManager?.cancel(pendingIntent)
        }
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

class TickGenerator(
    val onTick: () -> Unit
) {
    companion object {
        private const val MILLIS_IN_SECOND = 1000L
    }

    private var timer: Timer? = null

    fun start() {
        if (timer != null) throw TimerAlreadyRunningException()

        registerTickTimer()
    }

    fun cancel() {
        timer?.cancel()
        timer = null
    }

    private fun registerTickTimer() {
        timer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() = onTick()
            }, 0, MILLIS_IN_SECOND)
        }
    }
}

interface TimerListener {
    fun onTick()
    fun onTimeOver()
}

class CountdownTimer(
    private val timerListener: TimerListener,
    context: Context
) : TimerToggle {

    private var startedAtMillis = 0L
    private var timerDurationMillis = 0L
    val millisLeft get() = timerDurationMillis - (System.currentTimeMillis() - startedAtMillis)

    private var isFinished = false

    private val tickGenerator = TickGenerator(onTick = {
        if (millisLeft >= 0) timerListener.onTick()
        if (millisLeft <= 0) onTimeOver()
    })

    private val systemTimer = SystemTimer(context, onTimeOver = ::onTimeOver)

    override fun start(durationMillis: Long) {
        startedAtMillis = System.currentTimeMillis()
        timerDurationMillis = durationMillis

        tickGenerator.start()
        systemTimer.start(durationMillis)
    }

    fun pause() {
        cancel()
        timerDurationMillis = millisLeft
    }

    fun resume() {
        start(timerDurationMillis)
    }

    private fun onTimeOver() {
        if (isFinished) return

        isFinished = true

        tickGenerator.cancel()
        timerListener.onTimeOver()
    }

    override fun cancel() {
        tickGenerator.cancel()
        systemTimer.cancel()
    }
}

private fun Intent.appendPackageName(context: Context): Intent = apply {
    `package` = context.packageName
}