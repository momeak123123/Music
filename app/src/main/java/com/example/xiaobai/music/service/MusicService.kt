package com.example.xiaobai.music.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import com.danikula.videocache.HttpProxyCacheServer
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.config.Cookie
import com.example.xiaobai.music.config.Dencry
import com.example.xiaobai.music.config.Notifications
import com.example.xiaobai.music.music.view.act.MusicPlayActivity
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.ywl5320.wlmedia.WlMedia
import com.ywl5320.wlmedia.enums.WlComplete
import com.ywl5320.wlmedia.enums.WlPlayModel
import com.ywl5320.wlmedia.log.WlLog
import io.reactivex.Observable
import java.util.*


class MusicService : Service() {


    private var count: Int = 2
    private var id = 0
    lateinit var wlMedia: WlMedia
    var playingMusicList: MutableList<Music>? = null
    lateinit var t1: String
    lateinit var t2: String

    /** 标识是否可以使用onRebind  */
    private var mAllowRebind = false

    /** 当服务被创建时调用.  */
    override fun onCreate() {
        println("初始启动")
        wlMedia = WlMedia()
        wlMedia.setPlayModel(WlPlayModel.PLAYMODEL_ONLY_AUDIO) //设置只播放音频（必须）
        wlMedia.source = ""
        wlMedia.setOnPreparedListener {
            if (wlMedia.duration > 0) {
                MusicApp.setPress(0.0)
                Observable.just(wlMedia.duration.toLong()).subscribe(MusicPlayActivity.observerui)
                wlMedia.start()
                MusicApp.setPlay(true)
            } else {
                Toast.makeText(
                    MusicApp.getAppContext(),
                    getText(R.string.error_playing_track),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        wlMedia.setOnTimeInfoListener { currentTime, _ ->

            MusicApp.setPress(currentTime)
            Observable.just(currentTime).subscribe(MusicPlayActivity.observerseek)
        }

        wlMedia.setOnLoadListener { b ->
            if (b) {

                WlLog.d("加载中")
            } else {
                WlLog.d("加载完成")
                Observable.just(1).subscribe(MusicPlayActivity.observerplay)
            }
        }

        wlMedia.setOnErrorListener { _, _ ->
            WlLog.d("播放错误  0")
        }

        wlMedia.setOnCompleteListener { type ->
            when {
                type === WlComplete.WL_COMPLETE_EOF -> {
                    WlLog.d("正常播放结束   1")
                    Observable.just(2).subscribe(MusicPlayActivity.observerplay)
                    musicnext()
                }
                type === WlComplete.WL_COMPLETE_NEXT -> {
                    WlLog.d("切换下一首，导致当前结束   2")
                    Observable.just(2).subscribe(MusicPlayActivity.observerplay)
                }
                type === WlComplete.WL_COMPLETE_HANDLE -> {
                    WlLog.d("手动结束   3")
                    wlMedia.stop()
                    Toast.makeText(
                        MusicApp.getAppContext(),
                        getText(R.string.error_playing_track),
                        Toast.LENGTH_SHORT
                    ).show()
                    Observable.just(2).subscribe(MusicPlayActivity.observerplay)
                }
                type === WlComplete.WL_COMPLETE_ERROR -> {
                    WlLog.d("播放出现错误结束   4")
                    wlMedia.stop()
                    Observable.just(2).subscribe(MusicPlayActivity.observerplay)
                    musicnext()
                }
            }
        }
        wlMedia.prepared()

        //广播 添加广播的action
        val intentFilter = IntentFilter()
        intentFilter.addAction("del")
        intentFilter.addAction("pre")
        intentFilter.addAction("play")
        intentFilter.addAction("next")
        registerReceiver(broadcastReceiver, intentFilter)


        val intent = Intent(this, LockService::class.java)
        startService(intent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //创建NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "10898958",
                "小白音乐",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        startForeground(1, getNotification())

    }

    private fun getNotification(): Notification? {
        val builder: Notification.Builder = Notification.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("title")
            .setContentText("text")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("10898958")
        }
        return builder.build()
    }

    fun musicplay(type: Int, count: Int) {
        when (count) {
            0 -> {
                //单曲循环
                uriseat(playingMusicList!![id].uri, playingMusicList!![id].publish_time)
            }
            1 -> {
                //随机播放
                val randoms = (0 until playingMusicList!!.size).random()
                id = randoms
                data(id)
            }
            2 -> {
                if (type == 1) {
                    if (id == 0) {
                        id = playingMusicList!!.size - 1
                        data(id)

                    } else {
                        id -= 1
                        data(id)
                    }
                } else if (type == 2) {
                    if (playingMusicList!!.size - 1 == id) {
                        id = 0
                        data(id)
                    } else {
                        id += 1
                        data(id)
                    }

                }

            }
        }

    }

    fun data(ids: Int) {
        MusicApp.setPosition(ids)
        val artist = playingMusicList!![ids].all_artist
        var srtist_name = ""
        for (it in artist) {
            if (srtist_name != "") {
                srtist_name += "/" + it.name
            } else {
                srtist_name = it.name
            }

        }
        t1 = playingMusicList!![ids].name
        t2 = srtist_name
        Observable.just(true).subscribe(MusicPlayActivity.observers)
        uriseat(playingMusicList!![ids].uri, playingMusicList!![ids].publish_time)
    }

    fun uriseat(uri: String, time: String) {
        if (uri != "") {
            val proxy: HttpProxyCacheServer = getProxy()
            val proxyUrl = proxy.getProxyUrl(uri)
            wlMedia.source = proxyUrl
            wlMedia.next()

        } else {
            if (time != "") {
                musicpath(
                    time,
                    Cookie.getCookie()
                )
            } else {
                Observable.just(2).subscribe(MusicPlayActivity.observerplay)
            }


        }
    }


    fun musicpath(url: String, cookie: String) {
        object : Thread() {
            override fun run() {
                OkGo.post<String>(url)
                    .params("cookie", cookie)
                    .execute(object : StringCallback() {
                        override fun onSuccess(response: Response<String>) {
                            /**
                             * 成功回调
                             */
                            try {
                                val ca = response.body().substring(7)
                                val da = ca.substring(0, ca.lastIndexOf('<'))
                                val bean =
                                    Gson().fromJson(
                                        da,
                                        com.example.xiaobai.music.parsing.musicpath::class.javaObjectType
                                    )
                                val uri = Dencry.dencryptString(bean.geturl)
                                val proxy: HttpProxyCacheServer = getProxy()
                                val proxyUrl = proxy.getProxyUrl(uri)
                                wlMedia.source = proxyUrl
                                wlMedia.next()

                            } catch (e: Exception) {
                            }
                        }
                    })
            }
        }.start()
    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (Objects.requireNonNull(intent.action)) {
                "del" -> Notifications.deleteNotification()
                "pre" ->
                    musicpre()
                "play" ->{
                    if ( MusicApp.getPlay()) {
                        musicpause()
                    } else {
                        musicresume()
                    }
                }
                "next" ->
                    musicnext()
                else -> {
                }
            }
        }
    }



    private fun getProxy(): HttpProxyCacheServer {
        return MusicApp.getProxy(applicationContext)
    }

    fun musicresme() {
        println("恢复")
        Observable.just(true).subscribe(MusicPlayActivity.observers)
        if (MusicApp.getPlay()) {
            Observable.just(wlMedia.duration.toLong()).subscribe(MusicPlayActivity.observerui)
        }

    }

    fun musicstart(ids: Int) {
        println("切歌")
        if ( MusicApp.getPlay()) {
            wlMedia.stop()
        }
        MusicApp.setPosition(ids)
        id = ids
        playingMusicList = MusicApp.getMusic()
        uriseat(playingMusicList!![ids].uri, playingMusicList!![ids].publish_time)
        Observable.just(true).subscribe(MusicPlayActivity.observers)
    }

    fun musicnext() {
        println("下一首")
        if ( MusicApp.getPlay()) {
            wlMedia.stop()
        }
        musicplay(2, count)

    }

    fun musicpre() {
        println("上一首")
        if ( MusicApp.getPlay()) {
            wlMedia.stop()
        }
        musicplay(1, count)
    }

    fun musicresume() {
        println("继续")
        MusicApp.setPlay(true)
        wlMedia.resume()
        Notifications.init(1)
        Observable.just(4).subscribe(MusicPlayActivity.observerplay)
    }

    fun musicpause() {
        println("暂停")
        MusicApp.setPlay(false)
        wlMedia.pause()
        Notifications.init(0)
        Observable.just(3).subscribe(MusicPlayActivity.observerplay)
    }

    /** 调用startService()启动服务时回调  */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val types = intent!!.getIntExtra("type", 0)
        val ids = intent.getIntExtra("id", 0)
        count = intent.getIntExtra("count", 0)
        val seek = intent.getDoubleExtra("seek", 0.0)
        Observable.just(0).subscribe(MusicPlayActivity.observerplay)
        when (types) {
            0 -> {
                musicstart(ids)
            }
            1 -> {
                musicpre()
            }
            2 -> {
                musicnext()
            }
            3 -> {
                musicpause()
            }
            4 -> {
                musicresume()
            }
            5 -> {
                wlMedia.seek(seek)
                wlMedia.seekTimeCallBack(false)
            }
            6 -> {
                wlMedia.seek(seek)
                wlMedia.seekTimeCallBack(true)
                if (MusicApp.getPlay()) {
                    wlMedia.start()
                }
            }
            7 -> {
                musicresme()
            }
        }

        return super.onStartCommand(intent, flags, startId)
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
        wlMedia.exit()
        unregisterReceiver(broadcastReceiver)
    }


}
