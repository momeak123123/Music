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
import com.app.xiaobai.music.SearchIndexActivity
import com.app.xiaobai.music.bean.Music
import com.app.xiaobai.music.bean.kugoumusic
import com.app.xiaobai.music.bean.kugousearchs
import com.app.xiaobai.music.config.Cookie
import com.app.xiaobai.music.config.Dencry
import com.app.xiaobai.music.config.LogDownloadListeners
import com.app.xiaobai.music.config.Notifications
import com.app.xiaobai.music.music.view.act.MusicPlayActivity
import com.app.xiaobai.music.utils.CipherUtil
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.lzy.okserver.OkDownload
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import wseemann.media.FFmpegMediaPlayer
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class MusicService : Service() {


    private var min: Long =0
    private lateinit var mDisposable: Disposable
    private lateinit var mp: FFmpegMediaPlayer
    private lateinit var notification: Notification
    private var style: Int = 0
    private var count: Int = 2
    private var id = 0
    var playingMusicList: MutableList<Music>? = null
    lateinit var t1: String
    lateinit var t2: String
    var play:Boolean = false

    /** 标识是否可以使用onRebind  */
    private var mAllowRebind = false

    /** 当服务被创建时调用.  */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        println("初始启动")
        interval(0,0)
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
        Observable.just(true).subscribe(MusicPlayActivity.observers)
        uriseat(playingMusicList!![ids].uri, playingMusicList!![ids].publish_time, this)
    }

    fun prox(uri: String) {
        try {
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
        }catch (e:java.lang.Exception){}

    }

    fun music(uri :String ) {

        mp = FFmpegMediaPlayer()
        mp.setOnPreparedListener { mp ->
            if (mp.duration > 0) {
                MusicPlayActivity.load = true
                Observable.just(mp.duration.toLong()).subscribe(MusicPlayActivity.observerui)
                mp.start()
                println("时间"+mp.duration)
                interval(0L, mp.duration.toLong())
            } else {
                Toast.makeText(
                    MusicApp.getAppContext(),
                    getText(R.string.error_playing_track),
                    Toast.LENGTH_SHORT
                ).show()
                MusicPlayActivity.load = false
                min=0
                mDisposable.dispose()
                Observable.just(1).subscribe(MusicPlayActivity.observerplay)
            }
        }
        mp.setOnErrorListener { mp, what, extra ->
            mp.release()
            MusicPlayActivity.load = false
            mDisposable.dispose()
            min=0
            Observable.just(5).subscribe(MusicPlayActivity.observerplay)
            musicnext()
            false
        }
        mp.setOnCompletionListener {
            Observable.just(2).subscribe(MusicPlayActivity.observerplay)
            mDisposable.dispose()
            min=0
            musicnext()
        }


        try {
            mp.reset()
            mp.setDataSource(uri)
            mp.prepareAsync()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    fun interval(mins :Long , max :Long){

        mDisposable = Flowable.intervalRange(mins, max+1000, 0, 100, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { t ->
                min = t
                Observable.just(t*100).subscribe(MusicPlayActivity.observerseek)
            }
            .doOnComplete {
                if(max>0){
                    mp.stop()
                    Observable.just(2).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay)
                    musicnext()
                }

            }
            .subscribe()
    }

    private fun uriseat(uri: String, time: String, context: Context) {
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
                prox(uri)

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
                    if(SearchIndexActivity.type==0){
                        musicpath(
                            time,
                            Cookie.getCookie()
                        )
                    }else{
                        musicpaths(
                            time
                        )
                    }

                } else {
                    Observable.just(1).subscribe(MusicPlayActivity.observerplay)
                }

            }
        } else if (style == 4) {
            music(CipherUtil.decryptString(context, uri))
        }
    }


    fun musicpath(mid: String, cookie: String) {
        object : Thread() {
            override fun run() {
                OkGo.get<String>( "http://symusic.top/music.php?source=tencent&types=url&mid=$mid&br=hq")
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
                                val deyuri = uri.substring(0,4)
                                if(deyuri=="http"){
                                    MusicPlayActivity.uri = uri
                                    prox(uri)
                                }

                            } catch (e: Exception) {
                            }
                        }
                    })
            }
        }.start()
    }


    fun musicpaths(hash: String) {
        object : Thread() {
            override fun run() {
                OkGo.post<String>("https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=$hash&mid=de0201ff944d568d0cd157105909b990")
                    .headers("cookie", "kg_mid=de0201ff944d568d0cd157105909b990; kg_dfid=334ira2OkIpb107Swn2akQ7E; KuGooRandom=66541594367253652; Hm_lvt_aedee6983d4cfc62f509129360d6bb3d=1594363925,1594625008,1594966568,1595644242; ACK_SERVER_10015=%7B%22list%22%3A%5B%5B%22bjlogin-user.kugou.com%22%5D%5D%7D; kg_dfid_collect=d41d8cd98f00b204e9800998ecf8427e; kg_mid_temp=de0201ff944d568d0cd157105909b990; ACK_SERVER_10016=%7B%22list%22%3A%5B%5B%22bjreg-user.kugou.com%22%5D%5D%7D; ACK_SERVER_10017=%7B%22list%22%3A%5B%5B%22bjverifycode.service.kugou.com%22%5D%5D%7D; KuGoo=KugooID=1731378128&KugooPwd=25E317DED4B6894FADC70DCEED1053B0&NickName=%u6ed1%u843d%u4e4b%u540e&Pic=http://imge.kugou.com/kugouicon/165/20200728/20200728175658385542.jpg&RegState=1&RegFrom=&t=afa795b7f206723df3b2fdb31811517ef421b7d0421290119b2676174b5c77e4&a_id=1014&ct=1595930326&UserName=%u006b%u0067%u006f%u0070%u0065%u006e%u0031%u0037%u0033%u0031%u0033%u0037%u0038%u0031%u0032%u0038; KugooID=1731378128; t=afa795b7f206723df3b2fdb31811517ef421b7d0421290119b2676174b5c77e4; a_id=1014; UserName=%u006b%u0067%u006f%u0070%u0065%u006e%u0031%u0037%u0033%u0031%u0033%u0037%u0038%u0031%u0032%u0038; mid=de0201ff944d568d0cd157105909b990; dfid=334ira2OkIpb107Swn2akQ7E; Hm_lpvt_aedee6983d4cfc62f509129360d6bb3d=1595930330")
                    .execute(object : StringCallback() {
                        override fun onSuccess(response: Response<String>) {
                            /**
                             * 成功回调
                             */
                            try {
                                val bean =
                                    Gson().fromJson(response.body(), kugousearchs::class.javaObjectType)
                                if (bean.err_code == 0) {
                                    println("测试"+bean.data)
                                    val music = MusicPlayActivity.playingMusicList[MusicPlayActivity.id]
                                    music.pic_url = bean.data.get("img").asString
                                    MusicPlayActivity.playingMusicList[MusicPlayActivity.id] = music
                                    val uri = bean.data.get("play_url").asString
                                    if(uri!=""){
                                        MusicPlayActivity.uri = uri
                                        prox(uri)
                                    }
                                }

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
                    music(MusicApp.getUri())
                }
                "error" -> {
                    Observable.just(5).subscribe(MusicPlayActivity.observerplay)
                }
                else -> {
                }
            }
        }
    }

    fun musicresme() {
        println("恢复")
        Observable.just(true).subscribe(MusicPlayActivity.observers)
        if (MusicApp.getPlay()) {
            Observable.just(mp.duration.toLong()).subscribe(MusicPlayActivity.observerui)
        }

    }

    fun musicstart(ids: Int) {

        println("播放")
        if (MusicApp.getPlay()) {
            mp.release()
        }
        min=0
        mDisposable.dispose()
        MusicApp.setPosition(ids)
        id = ids
        playingMusicList = MusicApp.getMusic()
        uriseat(playingMusicList!![ids].uri, playingMusicList!![ids].publish_time, this)
        Observable.just(true).subscribe(MusicPlayActivity.observers)
    }

    fun musicnext() {

        println("下一首")
        min=0
        mDisposable.dispose()
        MusicPlayActivity.load = false
        Observable.just(0).subscribe(MusicPlayActivity.observerplay)
        if (MusicApp.getPlay()) {
            mp.stop()
        }
        musicplay(2, count)
    }

    fun musicpre() {

        println("上一首")
        min=0
        mDisposable.dispose()
        MusicPlayActivity.load = false
        Observable.just(0).subscribe(MusicPlayActivity.observerplay)
        if (MusicApp.getPlay()) {
            mp.stop()
        }
        musicplay(1, count)
    }

    fun musicresume() {
        println("继续")
        mp.start()
        interval(min, mp.duration.toLong())
        Notifications.init(1)
        Observable.just(4).subscribe(MusicPlayActivity.observerplay)
    }

    fun musicpause() {
        println("暂停")
        if (MusicApp.getPlay()) {
            mp.pause()
        }
        Notifications.init(0)
        Observable.just(3).subscribe(MusicPlayActivity.observerplay)
        mDisposable.dispose()
    }

    /** 调用startService()启动服务时回调  */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        play = true
        val types = intent!!.getIntExtra("type", 0)
        val ids = intent.getIntExtra("id", 0)
        count = intent.getIntExtra("count", 0)
        style = intent.getIntExtra("style", 0)
        val seek = intent.getLongExtra("seek", 0)
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
                mp.seekTo(seek.toInt())
                mDisposable.dispose()
                interval((seek/100), mp.duration.toLong())
                if (!mp.isPlaying) {
                    mp.start()
                    Notifications.init(1)
                    Observable.just(4).subscribe(MusicPlayActivity.observerplay)
                }
            }
            6 -> {
                musicresme()
            }
            7->{
                mp.release()
                mDisposable.dispose()
                min=0
                musicnext()
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
        mp.release()
        mDisposable.dispose()
        val lockservice = Intent(this, LockService::class.java)
        stopService(lockservice)

        Notifications.deleteNotification()

        unregisterReceiver(broadcastReceiver)
    }


}
