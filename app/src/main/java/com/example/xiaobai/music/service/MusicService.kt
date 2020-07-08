package com.example.xiaobai.music.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.danikula.videocache.HttpProxyCacheServer
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.music.view.act.MusicPlayActivity
import com.ywl5320.wlmedia.WlMedia
import com.ywl5320.wlmedia.enums.WlComplete
import com.ywl5320.wlmedia.enums.WlPlayModel
import com.ywl5320.wlmedia.enums.WlSampleRate
import com.ywl5320.wlmedia.log.WlLog


class MusicService : Service() {

    lateinit var wlMedia: WlMedia
    /** 标识服务如果被杀死之后的行为  */
    var mStartMode = 0



    /** 标识是否可以使用onRebind  */
    var mAllowRebind = false

    /** 绑定的客户端接口  */
    private val mBinder = MyBinder()

    /** 当服务被创建时调用.  */
    override fun onCreate() {
        try {
            val proxy: HttpProxyCacheServer = getProxy()
            val proxyUrl: String = proxy.getProxyUrl(MusicApp.getMusic()[MusicApp.getPosition()].uri)
            wlMedia = WlMedia()
            wlMedia.setPlayModel(WlPlayModel.PLAYMODEL_ONLY_AUDIO) //设置只播放音频（必须）
            wlMedia.setSampleRate(WlSampleRate.SAMPLE_RATE_48000)
            wlMedia.source = proxyUrl //设置数据源

            wlMedia.setOnPreparedListener {


            }
            wlMedia.setOnTimeInfoListener { currentTime, bufferTime ->


            }

            wlMedia.setOnLoadListener { b ->
                if (b) {
                    WlLog.d("加载中")
                } else {
                    WlLog.d("加载完成")
                }
            }

            wlMedia.setOnErrorListener { _, _ ->

            }

            wlMedia.setOnCompleteListener { type ->
                //
                when {
                    type === WlComplete.WL_COMPLETE_EOF -> {
                        WlLog.d("正常播放结束")

                    }
                    type === WlComplete.WL_COMPLETE_NEXT -> {
                        WlLog.d("切换下一首，导致当前结束")

                    }
                    type === WlComplete.WL_COMPLETE_HANDLE -> {
                        WlLog.d("手动结束")

                    }
                    type === WlComplete.WL_COMPLETE_ERROR -> {
                        WlLog.d("播放出现错误结束")

                    }
                }
            }

            wlMedia.prepared()

        } catch (e: Exception) {
        }
    }


    private fun getProxy(): HttpProxyCacheServer {
        return MusicApp.getProxy(applicationContext)
    }

    /** 调用startService()启动服务时回调  */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return mStartMode
    }

    /** 通过bindService()绑定到服务的客户端  */
    override fun onBind(intent: Intent?): IBinder? {
        val bundle = intent?.extras
        println( bundle?.get("dis") as String)
        return mBinder
    }

    /** 通过unbindService()解除所有客户端绑定时调用  */
    override fun onUnbind(intent: Intent?): Boolean {
        return mAllowRebind
    }

    /** 通过bindService()将客户端绑定到服务时调用 */
    override fun onRebind(intent: Intent?) {}

    /** 服务不再有用且将要被销毁时调用  */
    override fun onDestroy() {}


    internal class MyBinder : Binder() {

        fun startMusic() {

        }

        fun stopMusic() {

        }

        fun pauseMusic() {

        }

        fun resumeMusic() {

        }

        fun exitMusic() {

        }
    }
}
