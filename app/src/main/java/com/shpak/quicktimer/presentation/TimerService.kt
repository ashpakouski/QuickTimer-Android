package com.shpak.quicktimer.presentation

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.shpak.quicktimer.R
import com.shpak.quicktimer.data.timer.SingleUseCountdownTimer
import com.shpak.quicktimer.data.timer.TimerListener
import com.shpak.quicktimer.util.Debouncer
import com.shpak.quicktimer.util.getNotificationButton
import com.shpak.quicktimer.util.lazyTryOrNull
import com.shpak.quicktimer.util.playSound
import com.shpak.quicktimer.util.toHhMmSs

class TimerService : Service(), TimerListener {
    companion object {
        private const val ACTION_PAUSE = "action_pause"
        private const val ACTION_RESUME = "action_resume"
        private const val ACTION_CANCEL = "action_cancel"

        private const val KEY_TIME_MILLIS = "time_millis"

        private const val NOTIFICATION_ID = 7

        fun start(context: Context, timeMillis: Long) {
            val startIntent = Intent(context, TimerService::class.java)
            startIntent.putExtra(KEY_TIME_MILLIS, timeMillis)

            try {
                context.startForegroundService(startIntent)
            } catch (_: Exception) {
                Toast.makeText(
                    context, R.string.error_cant_start_timer_service, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val timer by lazyTryOrNull {
        SingleUseCountdownTimer(this, applicationContext)
    }

    private val notificationController by lazyTryOrNull {
        NotificationController(applicationContext)
    }

    private val notificationButtonPause by lazyTryOrNull {
        getNotificationButton(
            applicationContext, ACTION_PAUSE, R.string.notification_button_pause
        )
    }

    private val notificationButtonResume by lazyTryOrNull {
        getNotificationButton(
            applicationContext, ACTION_RESUME, R.string.notification_button_resume
        )
    }

    private val notificationButtonCancel by lazyTryOrNull {
        getNotificationButton(
            applicationContext, ACTION_CANCEL, R.string.notification_button_cancel
        )
    }

    private val buttonClickReceiver = object : BroadcastReceiver() {
        private val pauseResumeDebouncer = Debouncer(1000L)

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_PAUSE -> pauseResumeDebouncer.call { onPauseRequested() }
                ACTION_RESUME -> pauseResumeDebouncer.call { timer?.resume() }
                ACTION_CANCEL -> onCancellationRequested()
            }
        }
    }

    private fun onPauseRequested() {
        val timer = timer ?: return
        val millisLeft = timer.millisLeft

        timer.pause()

        notificationController?.postNotification(
            NOTIFICATION_ID, millisLeft.toHhMmSs(),
            actions = listOfNotNull(
                notificationButtonCancel, notificationButtonResume
            )
        )
    }

    private fun onCancellationRequested() {
        timer?.cancel()
        unregisterButtonClickReceiver()
        stopSelf()
    }

    override fun onCreate() {
        super.onCreate()

        notificationController?.getNotification("")?.let {
            startForeground(NOTIFICATION_ID, it)
        }

        registerButtonClickReceiver()
    }

    override fun onDestroy() {
        unregisterButtonClickReceiver()
        timer?.cancel()

        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (timer?.isRunning == false) {
            val durationMillis = intent?.getLongExtra(KEY_TIME_MILLIS, 0L) ?: 0
            timer?.start(
                durationMillis = durationMillis
            )
        } else {
            Toast.makeText(
                applicationContext, R.string.error_timer_is_already_running, Toast.LENGTH_LONG
            ).show()
        }

        return START_STICKY
    }

    override fun onTick() {
        val timer = timer ?: return

        notificationController?.postNotification(
            NOTIFICATION_ID, timer.millisLeft.toHhMmSs(),
            actions = listOfNotNull(
                notificationButtonCancel, notificationButtonPause
            )
        )
    }

    override fun onTimeOver() {
        notificationController?.postNotification(
            NOTIFICATION_ID, (0L).toHhMmSs()
        )

        playSound(applicationContext, R.raw.double_ping, onComplete = ::stopSelf)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun registerButtonClickReceiver() {
        ContextCompat.registerReceiver(
            applicationContext,
            buttonClickReceiver,
            IntentFilter().apply {
                addAction(ACTION_PAUSE)
                addAction(ACTION_RESUME)
                addAction(ACTION_CANCEL)
            },
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun unregisterButtonClickReceiver() {
        try {
            applicationContext.unregisterReceiver(buttonClickReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}