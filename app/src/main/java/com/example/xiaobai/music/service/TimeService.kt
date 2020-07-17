package com.example.xiaobai.music.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.*
import android.os.IBinder
import java.text.SimpleDateFormat
import java.util.*

class TimeService : Service() {

    private lateinit var sp: SharedPreferences
    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(receiver, filter)
        sp = getSharedPreferences("User", Context.MODE_PRIVATE)
    }

    override fun onDestroy() {
        super.onDestroy()
    }



    @SuppressLint("SimpleDateFormat")
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_TIME_TICK) {
                val df = SimpleDateFormat("HH:mm") //设置日期格式
                df.format(Date()).split(":").toTypedArray()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
    override fun onBind(intent: Intent): IBinder? {
       return null
    }
}
