package com.example.stopwatch


import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.*


class TimerService: Service() {

    override fun onBind(p0: Intent?): IBinder? = null

    private val timer = Timer()
    private var task: TimerTask? = null
    private var time = 0.0
    private var isRunning = false


        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            time = intent?.getDoubleExtra(TIME_EXTRA, 0.0)?: 0.0
            isRunning = true
            scheduleNextTick()
            return START_NOT_STICKY
        }

    @SuppressLint("DiscouragedApi")
    private fun scheduleNextTick() {
        task?.cancel()
        task = object : TimerTask() {
            override fun run() {
                if (!isRunning) return

                time+= 0.01
                val updateIntent = Intent(TIMER_UPDATED).apply {
                    setPackage(packageName)
                    putExtra(TIME_EXTRA, time)
                }
                sendBroadcast(updateIntent)

            }
        }
        timer.scheduleAtFixedRate(task, 7, 10)
    }

    override fun onDestroy() {
        isRunning = false
        task?.cancel()
        timer.cancel()
        super.onDestroy()
    }

    companion object {
            const val TIMER_UPDATED = "timerUpdated"
            const val TIME_EXTRA = "timeExtra"
        }
}