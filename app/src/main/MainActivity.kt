package com.example.stopwatch

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stopwatch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    private var allowUpdates = true
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.StartButton.setOnClickListener { StartTimer() }
        binding.ResetButton.setOnClickListener { ResetTimer() }

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU){
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED),Context.RECEIVER_NOT_EXPORTED)
        }
        else{
            registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun ResetTimer() {
        allowUpdates = false
        stopTimer()
        time = 0.0
        binding.timer.text = getTimeStringFromDouble(time)
    }

    private fun StartTimer() {
        if(timerStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer() {
        allowUpdates = true
        serviceIntent.putExtra(TimerService.TIME_EXTRA,time)
        startService(serviceIntent)
        binding.StartButton.text = "Stop"
        ContextCompat.getDrawable(this,R.drawable.baseline_pause_circle_24)
        timerStarted= true
    }

    private fun stopTimer() {
        stopService(serviceIntent)
        binding.StartButton.text = "Start"
        binding.StartButton.icon = getDrawable(R.drawable.baseline_play_arrow_24)
        timerStarted= false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (!allowUpdates) return
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            runOnUiThread {
                binding.timer.text = getTimeStringFromDouble(time)
            }
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultMillis = (time * 1000).toInt()
        val minutes = resultMillis / 60000
        val seconds = (resultMillis % 60000) / 1000
        val milliseconds = resultMillis % 1000


        return makeTimeString(minutes, seconds, milliseconds)
    }

        private fun makeTimeString( minutes: Int, seconds: Int, milliseconds: Int): String = String.format("%02d:%02d:%03d",minutes,seconds,milliseconds)
    }
