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
import com.app.xiaobai.music.MusicApp
import com.app.xiaobai.music.R
import com.app.xiaobai.music.bean.Music
import com.app.xiaobai.music.config.Cookie
import com.app.xiaobai.music.config.Dencry
import com.app.xiaobai.music.config.LogDownloadListeners
import com.app.xiaobai.music.config.Notifications
import com.app.xiaobai.music.music.view.act.MusicPlayActivity
import com.app.xiaobai.music.utils.CipherUtil
import com.danikula.videocache.HttpProxyCacheServer
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.lzy.okserver.OkDownload
import com.ywl5320.libenum.MuteEnum
import com.ywl5320.libmusic.WlMusic
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


class MusicService : Service() {


    private var min: Long = 0
    private lateinit var mDisposable: Disposable
    private lateinit var notification: Notification
    private var style: Int = 0
    private var count: Int = 2
    private var id = 0
    lateinit var wlMusic: WlMusic
    var playingMusicList: MutableList<Music>? = null
    lateinit var t1: String
    lateinit var t2: String

    /** 标识是否可以使用onRebind  */
    private var mAllowRebind = false

    /** 当服务被创建时调用.  */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        println("初始启动")
        interval(0,0)
        wlMusic = WlMusic.getInstance()
        wlMusic.source = "" //设置音频源
        wlMusic.setCallBackPcmData(false) //是否返回音频PCM数据
        wlMusic.setShowPCMDB(false) //是否返回音频分贝大小
        wlMusic.isPlayCircle = false //设置不间断循环播放音频
        wlMusic.volume = 100 //设置音量 65%
        wlMusic.playSpeed = 1.0f //设置播放速度 (1.0正常) 范围：0.25---4.0f
        wlMusic.playPitch = 1.0f //设置播放速度 (1.0正常) 范围：0.25---4.0f
        wlMusic.mute = MuteEnum.MUTE_CENTER //设置立体声（左声道、右声道和立体声）
        wlMusic.setConvertSampleRate(null) //设定恒定采样率（null为取消）

        wlMusic.setOnPreparedListener {

            if (wlMusic.duration > 0) {
                MusicPlayActivity.load = true
                Observable.just(wlMusic.duration.toLong()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerui)
                wlMusic.start()
                interval(0,wlMusic.duration.toLong())
            } else {
                Toast.makeText(
                    MusicApp.getAppContext(),
                    getText(R.string.error_playing_track),
                    Toast.LENGTH_SHORT
                ).show()
                mDisposable.dispose()
                MusicPlayActivity.load = false
                Observable.just(1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay)
            }

        }

        wlMusic.setOnLoadListener { b ->
            if (!b) {
                Observable.just(4).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay)
            }
        }

        wlMusic.setOnErrorListener { code, msg ->
            MusicPlayActivity.load = false
            mDisposable.dispose()
            Observable.just(2).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay)
        }

        wlMusic.setOnCompleteListener {

        }

        wlMusic.prePared()

        //广播 添加广播的action
        val intentFilter = IntentFilter()
        intentFilter.addAction("del")
        intentFilter.addAction("pre")
        intentFilter.addAction("play")
        intentFilter.addAction("next")
        intentFilter.addAction("uri")
        intentFilter.addAction("error")
        registerReceiver(broadcastReceiver, intentFilter)


        val intent = Intent(this, LockService::class.java)
        startService(intent)


        val notificationChannel: NotificationChannel?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(
                    "xiaobai1089",
                    getText(R.string.app_name).toString(), NotificationManager.IMPORTANCE_MIN
                )
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

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
    }

    fun interval(mins :Long , max :Long){
        mDisposable = Flowable.intervalRange(mins, max, 0, 1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { t ->
                min = t
                Observable.just(t).subscribe(MusicPlayActivity.observerseek)
            }
            .doOnComplete {
                if(max>0){
                    wlMusic.stop()
                    Observable.just(2).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay)
                    musicnext()
                }

            }
            .subscribe()
    }

    fun musicplay(type: Int, count: Int) {
        when (count) {
            0 -> {
                //单曲循环
                uriseat(playingMusicList!![id].uri, playingMusicList!![id].publish_time, this)
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
        Observable.just(true).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observers)
        uriseat(playingMusicList!![ids].uri, playingMusicList!![ids].publish_time, this)
    }

    fun prox(uri: String) {
        val path = cacheDir.absolutePath
        val file = File(path)
        if (file.isDirectory) {
            val files: Array<File> = file.listFiles()
            if(files.size>20){
                for (i in files.indices) {
                    val f = files[i]
                    try {
                        f.delete()
                    } catch (e: Exception) {
                    }
                }
            }
        }
        val request = OkGo.get<File>(uri)

        OkDownload.request(
            uri,
            request
        )
            .priority(0)
            .folder(cacheDir.absolutePath)
            .fileName(System.currentTimeMillis().toString()) //
            .save() //
            .register(
                LogDownloadListeners()
            )
            .start()
    }

    fun music() {
        wlMusic.playNext(MusicApp.getUri())
    }

    fun uriseat(uri: String, time: String, context: Context) {
        if (style == 1) {
            if (MusicApp.network() == -1) {
                Toast.makeText(
                    MusicApp.getAppContext(),
                    getText(R.string.error_connection),
                    Toast.LENGTH_SHORT
                ).show()
                Observable.just(2).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay)
            } else {
                MusicPlayActivity.uri = uri
                val proxy: HttpProxyCacheServer = getProxy()
                val proxyUrl = proxy.getProxyUrl(uri, true)
                wlMusic.playNext(proxyUrl)
            }

        } else if (style == 3) {
            if (MusicApp.network() == -1) {
                Toast.makeText(
                    MusicApp.getAppContext(),
                    getText(R.string.error_connection),
                    Toast.LENGTH_SHORT
                ).show()
                Observable.just(2).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay)
            } else {
                if (time != "") {

                    musicpath(
                        time,
                        Cookie.getCookie()
                    )
                } else {
                    Observable.just(1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay)
                }

            }
        } else if (style == 4) {

            wlMusic.playNext(CipherUtil.decryptString(context, uri))
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
                                        com.app.xiaobai.music.bean.musicpath::class.javaObjectType
                                    )
                                val uri = Dencry.dencryptString(bean.geturl)
                                MusicPlayActivity.uri = uri
                                prox(uri)
                                /* val proxy: HttpProxyCacheServer = getProxy()
                                 val proxyUrl = proxy.getProxyUrl(uri, true)
                                 wlMedia.source = proxyUrl
                                 wlMedia.next()*/

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
                "uri" -> {
                    music()
                }
                "error" -> {
                    Observable.just(5).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay)
                }
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
        Observable.just(true).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observers)
        if (MusicApp.getPlay()) {
            Observable.just(wlMusic.duration.toLong()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerui)
        }

    }

    fun musicstart(ids: Int) {

        println("切歌")
        if (MusicApp.getPlay()) {
            wlMusic.stop()
        }
        MusicApp.setPosition(ids)
        id = ids
        playingMusicList = MusicApp.getMusic()
        uriseat(playingMusicList!![ids].uri, playingMusicList!![ids].publish_time, this)
        mDisposable.dispose()
        Observable.just(true).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observers)
    }

    fun musicnext() {

        println("下一首")
        MusicPlayActivity.load = false
        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay)
        if (MusicApp.getPlay()) {
            wlMusic.stop()
        }
        mDisposable.dispose()
        musicplay(2, count)


    }

    fun musicpre() {

        println("上一首")
        MusicPlayActivity.load = false
        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay)
        if (MusicApp.getPlay()) {
            wlMusic.stop()
        }
        mDisposable.dispose()
        musicplay(1, count)

    }

    fun musicresume() {
        println("继续")
        wlMusic.resume()
        interval(min,wlMusic.duration.toLong())
        Notifications.init(1)
        Observable.just(4).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay)
    }

    fun musicpause() {
        println("暂停")
        wlMusic.pause()
        mDisposable.dispose()
        Notifications.init(0)
        Observable.just(3).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay)
    }

    /** 调用startService()启动服务时回调  */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val types = intent!!.getIntExtra("type", 0)
        val ids = intent.getIntExtra("id", 0)
        count = intent.getIntExtra("count", 0)
        style = intent.getIntExtra("style", 0)
        val seek = intent.getIntExtra("seek", 0)
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
                wlMusic.seek(seek,false,false)
            }
            6 -> {
                wlMusic.seek(seek,true,true)
                mDisposable.dispose()

                interval(seek.toLong(),wlMusic.duration.toLong())
                if (MusicApp.getPlay()) {
                    wlMusic.start()
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
        if (MusicApp.getPlay()) {
            wlMusic.stop()
        }
        mDisposable.dispose()
        val lockservice = Intent(this, LockService::class.java)
        stopService(lockservice)

        Notifications.deleteNotification()

        unregisterReceiver(broadcastReceiver)
    }


}
