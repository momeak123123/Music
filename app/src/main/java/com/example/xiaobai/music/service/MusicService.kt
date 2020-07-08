package com.example.xiaobai.music.service

import android.app.Service
import android.content.Intent
import android.os.IBinder


class MusicService : Service() {


    private val tag = true
    var startIntent: Intent? = null

    /** 标识服务如果被杀死之后的行为  */
    var mStartMode = 0


    /** 标识是否可以使用onRebind  */
    private var mAllowRebind = false

    /** 当服务被创建时调用.  */
    override fun onCreate() {
        Thread(Runnable {
            while (tag) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()
    }

    /** 调用startService()启动服务时回调  */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return mStartMode
    }

    /** 通过bindService()绑定到服务的客户端 */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /** 通过unbindService()解除所有客户端绑定时调用  */
    override fun onUnbind(intent: Intent?): Boolean {
        return mAllowRebind
    }

    /** 通过bindService()将客户端绑定到服务时调用 */
    override fun onRebind(intent: Intent?) {

    }

    /** 服务不再有用且将要被销毁时调用  */
    override fun onDestroy() {
        if(startIntent!=null){
            startService(startIntent)
        }
    }

}
