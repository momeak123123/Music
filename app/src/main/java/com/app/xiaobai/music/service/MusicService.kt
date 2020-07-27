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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.app.xiaobai.music.MusicApp
import com.app.xiaobai.music.R
import com.app.xiaobai.music.bean.Music
import com.app.xiaobai.music.bean.musicpath
import com.app.xiaobai.music.config.Cookie
import com.app.xiaobai.music.config.Dencry
import com.app.xiaobai.music.config.LogDownloadListeners
import com.app.xiaobai.music.config.Notifications
import com.app.xiaobai.music.music.view.act.MusicPlayActivity
import com.app.xiaobai.music.music.view.act.MusicPlayActivity.Companion.observerplay
import com.app.xiaobai.music.utils.CipherUtil
import com.google.gson.Gson
import com.lzx.starrysky.StarrySky
import com.lzx.starrysky.common.PlaybackStage
import com.lzx.starrysky.control.RepeatMode.Companion.REPEAT_MODE_NONE
import com.lzx.starrysky.control.RepeatMode.Companion.REPEAT_MODE_ONE
import com.lzx.starrysky.control.RepeatMode.Companion.REPEAT_MODE_SHUFFLE
import com.lzx.starrysky.provider.SongInfo
import com.lzx.starrysky.utils.TimerTaskManager
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.GetRequest
import com.lzy.okserver.OkDownload
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.File
import java.util.*

class MusicService : Service() {

    private var count = 0
    private var style = 2
    private var types = 0
    private var ids = 0
    private var seek = 0L
    private lateinit var playingMusicList: MutableList<Music>
    private var id = 0
    private lateinit var mTimerTask: TimerTaskManager
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        println("初始化")
        mTimerTask = TimerTaskManager()

        StarrySky.with().playbackState()
            .observe((this as LifecycleOwner), Observer { playbackStage: PlaybackStage? ->
                if (playbackStage == null) {
                    return@Observer
                }
                when (Objects.requireNonNull(playbackStage.getStage())) {
                    PlaybackStage.NONE ->                     //空状态
                        Observable.just(1)
                            .subscribe(MusicPlayActivity.observerplay)
                    PlaybackStage.START -> {
                        //开始播放
                        println("开始播放")
                        MusicPlayActivity.load = "start"
                        mTimerTask.startToUpdateProgress()
                        val duration = StarrySky.with().getDuration()
                        Observable.just(duration)
                            .subscribe(MusicPlayActivity.observerui)
                    }
                    PlaybackStage.PAUSE -> {
                        //暂停
                        MusicPlayActivity.load = "pause"
                        mTimerTask.stopToUpdateProgress()
                        Observable.just(1)
                            .subscribe(MusicPlayActivity.observerplay)
                    }
                    PlaybackStage.STOP -> {
                        //停止
                        MusicPlayActivity.load = "stop"
                        mTimerTask.stopToUpdateProgress()
                        Observable.just(1)
                            .subscribe(MusicPlayActivity.observerplay)
                    }
                    PlaybackStage.COMPLETION -> {
                        //播放完成
                        println("播放完成")
                        mTimerTask.stopToUpdateProgress()
                        if (StarrySky.with().isSkipToNextEnabled()) {
                            Observable.just(0)
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .subscribe(MusicPlayActivity.observerplay)
                            if (MusicApp.getPlay()) {
                                StarrySky.with().stopMusic()
                            }
                            StarrySky.with().skipToNext()
                        }
                    }
                    PlaybackStage.BUFFERING -> {
                    }
                    PlaybackStage.ERROR -> {
                        //播放出错
                        println("播放出错")
                        mTimerTask.stopToUpdateProgress()
                        Observable.just(2)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(MusicPlayActivity.observerplay)
                    }
                    else -> {
                    }
                }
            })


        StarrySky.with().prepare()

        mTimerTask.setUpdateProgressTask(Runnable {
            val position = StarrySky.with().getPlayingPosition()
            val buffered = StarrySky.with().getBufferedPosition()
            Observable.just(position)
                .subscribe(MusicPlayActivity.observerseek)
            Observable.just(buffered)
                .subscribe(MusicPlayActivity.observerseeks)
        })


        StarrySky.with().setRepeatMode(REPEAT_MODE_NONE, true)

        val music: List<Music> = MusicPlayActivity.playingMusicList
        val infolist: MutableList<SongInfo> = ArrayList()
        for (i in music.indices) {
            val info = SongInfo()
            info.songId = music[i].song_id.toString()
            info.songUrl = music[i].uri
            infolist.add(info)
        }

        StarrySky.with().playMusic(infolist, 0)

        val intentFilter = IntentFilter()
        intentFilter.addAction("del")
        intentFilter.addAction("pre")
        intentFilter.addAction("play")
        intentFilter.addAction("next")
        intentFilter.addAction("uri")
        intentFilter.addAction("error")
        registerReceiver(broadcastReceiver, intentFilter)

        val intent = Intent(this as Context, LockService::class.java)
        startService(intent)
        val notificationChannel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                "xiaobai1089",
                getText(R.string.app_name).toString(),
                NotificationManager.IMPORTANCE_MIN
            )
            val notificationManager =
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            notificationManager.createNotificationChannel(notificationChannel)
            val notification: Notification =
                Notification.Builder(this, "xiaobai1089")
                    .setContentTitle("This is content title")
                    .setContentText("This is content text")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            resources,
                            R.mipmap.ic_launcher
                        )
                    )
                    .setOnlyAlertOnce(true)
                    .build()
            this.startForeground(1, notification)
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val string =
                Objects.requireNonNull(intent.action)
            when (string) {
                "del" -> {
                    stopForeground(true)
                }
                "pre" -> {
                    if (StarrySky.with().isSkipToPreviousEnabled()) {
                        Observable.just(0)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(observerplay)
                        if (MusicApp.getPlay()) {
                            StarrySky.with().stopMusic()
                        }
                        StarrySky.with().skipToPrevious()
                    }
                }
                "play" -> {
                    val bl = MusicApp.getPlay()
                    if (bl) {
                        StarrySky.with().pauseMusic()
                        Notifications.init(0)
                        Observable.just(3)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(observerplay)
                    } else {
                        StarrySky.with().restoreMusic()
                        Notifications.init(1)
                        Observable.just(4)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(observerplay)
                    }
                }
                "next" -> {
                    if (StarrySky.with().isSkipToNextEnabled()) {
                        Observable.just(0)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(observerplay)
                        if (MusicApp.getPlay()) {
                            StarrySky.with().stopMusic()
                        }
                        StarrySky.with().skipToNext()
                    }
                }
                "uri" -> {
                }
                "error" -> {
                    Observable.just(5)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(observerplay)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        types = intent!!.getIntExtra("type", 0)
        ids = intent.getIntExtra("id", 0)
        count = intent.getIntExtra("count", 0)
        style = intent.getIntExtra("style", 0)
        seek = intent.getLongExtra("seek", 0)

        when (types) {
            0 -> {
                try {
                    musicstart(ids)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            1 -> {
                if (StarrySky.with().isSkipToPreviousEnabled()) {
                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(observerplay)
                    if (MusicApp.getPlay()) {
                        StarrySky.with().stopMusic()
                    }
                    StarrySky.with().skipToPrevious()
                }
            }
            2 -> {
                if (StarrySky.with().isSkipToNextEnabled()) {
                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(observerplay)
                    if (MusicApp.getPlay()) {
                        StarrySky.with().stopMusic()
                    }
                    StarrySky.with().skipToNext()
                }
            }
            3 -> {
                StarrySky.with().pauseMusic()
                Notifications.init(0)
                Observable.just(3).subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(observerplay)
            }
            4 -> {
                StarrySky.with().restoreMusic()
                Notifications.init(1)
                Observable.just(4).subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(observerplay)
            }
            5 -> {
                StarrySky.with().seekTo(seek)
            }
            6 -> {
                when (count) {
                    0 -> StarrySky.with().setRepeatMode(REPEAT_MODE_ONE, true)
                    1 -> StarrySky.with().setRepeatMode(REPEAT_MODE_SHUFFLE, true)
                    2 -> StarrySky.with().setRepeatMode(REPEAT_MODE_NONE, true)
                }
            }
            7 -> {
                musicresme()
            }
            8 -> {
                try {
                    val music: List<Music> = MusicPlayActivity.playingMusicList
                    val infolist: MutableList<SongInfo> =
                        ArrayList()
                    var i = 0
                    while (i < music.size) {
                        val info = SongInfo()
                        info.songId = "0"
                        info.songUrl = ""
                        infolist.add(info)
                        i++
                    }
                    StarrySky.with().updatePlayList(infolist)
                    musicstart(ids)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun musicresme() {
        println("恢复")
        Observable.just(true)
            .subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observers)
        if (MusicApp.getPlay()) {
            Observable.just(StarrySky.with().getDuration())
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerui)
        }
    }


    @Throws(java.lang.Exception::class)
    private fun musicstart(ids: Int) {
        println("切歌")
        if (MusicApp.getPlay()) {
            StarrySky.with().stopMusic()
        }
        MusicApp.setPosition(ids)
        id = ids
        playingMusicList = MusicApp.getMusic()
        val info = SongInfo()
        info.songId = playingMusicList[ids].song_id.toString()
        info.songUrl = playingMusicList[ids].uri
        uriseat(info, playingMusicList[ids].publish_time, this)
        Observable.just(true)
            .subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observers)
    }

    @Throws(java.lang.Exception::class)
    private fun uriseat(
        info: SongInfo,
        time: String,
        context: Context
    ) {
        if (style == 1) {
            if (MusicApp.network() == -1) {
                Toast.makeText(
                    MusicApp.getAppContext(),
                    getText(R.string.error_connection),
                    Toast.LENGTH_SHORT
                ).show()
                Observable.just(2).subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(observerplay)
            } else {
                MusicPlayActivity.uri = info.songUrl
                StarrySky.with().playMusicByInfo(info)
            }
        } else if (style == 3) {
            if (MusicApp.network() == -1) {
                Toast.makeText(
                    MusicApp.getAppContext(),
                    getText(R.string.error_connection),
                    Toast.LENGTH_SHORT
                ).show()
                Observable.just(2).subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(observerplay)
            } else {
                if (time != "") {
                    musicpath(info, time, Cookie.getCookie())
                } else {
                    Observable.just(1).subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(observerplay)
                }
            }
        } else if (style == 4) {
            info.songUrl = CipherUtil.decryptString(context, info.songUrl)
            StarrySky.with().playMusicByInfo(info)
        }
    }

    private fun musicpath(
        info: SongInfo,
        url: String,
        cookie: String
    ) {
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
                        val gson = Gson()
                        val (_, _, geturl) = gson.fromJson(da, musicpath::class.java)
                        val uri = Dencry.dencryptString(geturl)
                        MusicPlayActivity.uri = uri
                        info.songUrl = uri
                        StarrySky.with().playMusicByInfo(info)
                        //prox(uri);
                    } catch (ignored: java.lang.Exception) {
                    }
                }
            })
    }

    private fun prox(uri: String) {
        val path = this.cacheDir.absolutePath
        val file = File(path)
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files.size > 20) {
                for (f in files) {
                    try {
                        f.delete()
                    } catch (ignored: java.lang.Exception) {
                    }
                }
            }
        }
        val request = OkGo.get<File>(uri)
        OkDownload.request(uri, request)
            .priority(0)
            .folder(file.absolutePath)
            .fileName(System.currentTimeMillis().toString())
            .save()
            .register(LogDownloadListeners())
            .start()
    }


    override fun onDestroy() {
        super.onDestroy()
        mTimerTask.removeUpdateProgressTask()
        val lockservice = Intent(this, LockService::class.java)
        stopService(lockservice)
        Notifications.deleteNotification()
    }
}
