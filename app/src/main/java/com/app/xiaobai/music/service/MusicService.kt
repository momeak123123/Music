package com.app.xiaobai.music.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.danikula.videocache.HttpProxyCacheServer
import com.app.xiaobai.music.MusicApp
import com.app.xiaobai.music.R
import com.app.xiaobai.music.bean.Music
import com.app.xiaobai.music.config.Cookie
import com.app.xiaobai.music.config.Dencry
import com.app.xiaobai.music.config.Notifications
import com.app.xiaobai.music.music.view.act.MusicPlayActivity
import com.app.xiaobai.music.utils.CipherUtil
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


    private lateinit var notification: Notification
    private var style: Int = 0
    private var count: Int = 2
    private var id = 0
    lateinit var wlMedia: WlMedia
    var playingMusicList: MutableList<Music>? = null
    lateinit var t1: String
    lateinit var t2: String

    /** 标识是否可以使用onRebind  */
    private var mAllowRebind = false

    /** 当服务被创建时调用.  */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        println("初始启动")
        wlMedia = WlMedia()
        wlMedia.setPlayModel(WlPlayModel.PLAYMODEL_ONLY_AUDIO) //设置只播放音频（必须）
        wlMedia.source = ""
        wlMedia.setOnPreparedListener {
            if (wlMedia.duration > 0) {
                MusicPlayActivity.load = true
                Observable.just(wlMedia.duration.toLong()).subscribe(MusicPlayActivity.observerui)
                wlMedia.start()
            } else {
                Toast.makeText(
                    MusicApp.getAppContext(),
                    getText(R.string.error_playing_track),
                    Toast.LENGTH_SHORT
                ).show()
                MusicPlayActivity.load = false
                Observable.just(1).subscribe(MusicPlayActivity.observerplay)
            }

        }
        wlMedia.setOnTimeInfoListener { currentTime, _ ->
            Observable.just(currentTime).subscribe(MusicPlayActivity.observerseek)
        }

        wlMedia.setOnLoadListener { b ->
            if (b) {
                WlLog.d("Loading")
            } else {
                WlLog.d("Loading carry")
                Observable.just(4).subscribe(MusicPlayActivity.observerplay)
            }
        }

        wlMedia.setOnErrorListener { code, msg ->
            WlLog.d("playback error")
            WlLog.d("code$code - msg$msg")
        }

        wlMedia.setOnCompleteListener { type ->
            when {
                type === WlComplete.WL_COMPLETE_EOF -> {
                    WlLog.d("Normal playback ends 1")
                    Observable.just(2).subscribe(MusicPlayActivity.observerplay)
                    musicnext()
                }
                type === WlComplete.WL_COMPLETE_NEXT -> {
                    WlLog.d("Switch to the next song, causing the current end   2")
                    Observable.just(2).subscribe(MusicPlayActivity.observerplay)
                }
                type === WlComplete.WL_COMPLETE_HANDLE -> {
                    WlLog.d("End manually   3")
                    MusicPlayActivity.load = false
                    Observable.just(2).subscribe(MusicPlayActivity.observerplay)
                }
                type === WlComplete.WL_COMPLETE_ERROR -> {
                    WlLog.d("Play ended with an error   4")
                    MusicPlayActivity.load = false
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


        val notificationChannel: NotificationChannel?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel("xiaobai1089",
                    getText(R.string.app_name).toString(), NotificationManager.IMPORTANCE_MIN)
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

         notification = Notification.Builder(this, "xiaobai1089")
                .setContentTitle("This is content title")
                .setContentText("This is content text")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setOnlyAlertOnce(true)
                .build()

        startForeground(1, notification)

    }

    fun musicplay(type: Int, count: Int) {
        when (count) {
            0 -> {
                //单曲循环
                uriseat(playingMusicList!![id].uri, playingMusicList!![id].publish_time,this)
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
        uriseat(playingMusicList!![ids].uri, playingMusicList!![ids].publish_time,this)
    }

    fun uriseat(uri: String, time: String, context : Context) {
        if (style == 1) {
            if (MusicApp.network() == -1) {
                Toast.makeText(
                    MusicApp.getAppContext(),
                    getText(R.string.error_connection),
                    Toast.LENGTH_SHORT
                ).show()
                Observable.just(2).subscribe(MusicPlayActivity.observerplay)
            } else {
                MusicPlayActivity.uri = uri
                val proxy: HttpProxyCacheServer = getProxy()
                val proxyUrl = proxy.getProxyUrl(uri, true)
                wlMedia.source = proxyUrl
                wlMedia.next()
            }

        } else if (style == 3) {
            if (MusicApp.network() == -1) {
                Toast.makeText(
                    MusicApp.getAppContext(),
                    getText(R.string.error_connection),
                    Toast.LENGTH_SHORT
                ).show()
                Observable.just(2).subscribe(MusicPlayActivity.observerplay)
            } else {
                if (time != "") {
                    musicpath(
                        time,
                        Cookie.getCookie()
                    )
                } else {
                    Observable.just(1).subscribe(MusicPlayActivity.observerplay)
                }

            }
        } else if (style == 4) {

            wlMedia.source = CipherUtil.decryptString(context,uri)
            wlMedia.next()
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
                                        com.app.xiaobai.music.parsing.musicpath::class.javaObjectType
                                    )
                                val uri = Dencry.dencryptString(bean.geturl)
                                MusicPlayActivity.uri = uri
                                val proxy: HttpProxyCacheServer = getProxy()
                                val proxyUrl = proxy.getProxyUrl(uri, true)
                                wlMedia.source = proxyUrl
                                wlMedia.next()

                            } catch (e: Exception) {
                            }
                        }
                    })
            }
        }.start()
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (Objects.requireNonNull(intent.action)) {
                "del" -> {
                    stopForeground(true)
                    //Notifications.deleteNotification()
                }
                "pre" ->
                    musicpre()
                "play" -> {
                    if (MusicApp.getPlay()) {
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
        if (MusicApp.getPlay()) {
            wlMedia.stop()
        }
        MusicApp.setPosition(ids)
        id = ids
        playingMusicList = MusicApp.getMusic()
        uriseat(playingMusicList!![ids].uri, playingMusicList!![ids].publish_time,this)
        Observable.just(true).subscribe(MusicPlayActivity.observers)
    }

    fun musicnext() {

        println("下一首")
        MusicPlayActivity.load = false
        Observable.just(0).subscribe(MusicPlayActivity.observerplay)
        if (MusicApp.getPlay()) {
            wlMedia.stop()
        }
        musicplay(2, count)


    }

    fun musicpre() {

        println("上一首")
        MusicPlayActivity.load = false
        Observable.just(0).subscribe(MusicPlayActivity.observerplay)
        if (MusicApp.getPlay()) {
            wlMedia.stop()
        }
        musicplay(1, count)

    }

    fun musicresume() {
        println("继续")
        wlMedia.resume()
        Notifications.init(1)
        Observable.just(4).subscribe(MusicPlayActivity.observerplay)
    }

    fun musicpause() {
        println("暂停")
        wlMedia.pause()
        Notifications.init(0)
        Observable.just(3).subscribe(MusicPlayActivity.observerplay)
    }

    /** 调用startService()启动服务时回调  */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val types = intent!!.getIntExtra("type", 0)
        val ids = intent.getIntExtra("id", 0)
        count = intent.getIntExtra("count", 0)
        style = intent.getIntExtra("style", 0)
        val seek = intent.getDoubleExtra("seek", 0.0)
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

        val lockservice = Intent(this, LockService::class.java)
        stopService(lockservice)

        Notifications.deleteNotification()

        unregisterReceiver(broadcastReceiver)
    }


}