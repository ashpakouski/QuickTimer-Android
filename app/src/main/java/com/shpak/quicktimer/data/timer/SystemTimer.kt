package com.shpak.quicktimer.data.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat

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

private fun Intent.appendPackageName(context: Context): Intent = apply {
    `package` = context.packageName
}