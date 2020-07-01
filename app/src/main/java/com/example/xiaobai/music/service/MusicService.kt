package com.example.xiaobai.music.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class MusicService : Service() {

    override fun onBind(intent: Intent): IBinder {
       return MusicService()
    }

    override fun onCreate() {
        super.onCreate()
    }

    //该方法包含关于歌曲的操作
     class MusicService : Binder() {

    }


}
