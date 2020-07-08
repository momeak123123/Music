package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.*
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.danikula.videocache.CacheListener
import com.danikula.videocache.HttpProxyCacheServer
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.PlayListAdapter
import com.example.xiaobai.music.adapter.PlaySongAdapter
import com.example.xiaobai.music.adapter.ViewPagerAdapter
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.config.Cookie
import com.example.xiaobai.music.config.Dencry
import com.example.xiaobai.music.config.LogDownloadListener
import com.example.xiaobai.music.config.Notification
import com.example.xiaobai.music.music.model.MusicPlayModel
import com.example.xiaobai.music.music.view.fragment.CoverFragment
import com.example.xiaobai.music.music.view.fragment.LyricFragment
import com.example.xiaobai.music.service.LockService
import com.example.xiaobai.music.service.MusicService
import com.example.xiaobai.music.sql.bean.Playlist
import com.example.xiaobai.music.sql.dao.mDownDao
import com.example.xiaobai.music.sql.dao.mPlaylistDao
import com.example.xiaobai.music.utils.BitmapUtils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import com.lzy.okgo.OkGo
import com.lzy.okserver.OkDownload
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.ywl5320.wlmedia.WlMedia
import com.ywl5320.wlmedia.enums.WlComplete
import com.ywl5320.wlmedia.enums.WlPlayModel
import com.ywl5320.wlmedia.enums.WlSampleRate
import com.ywl5320.wlmedia.log.WlLog
import io.alterac.blurkit.BlurKit
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.frag_player_lrcview.*
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.music_play.*
import kotlinx.android.synthetic.main.play_list.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class MusicPlayActivity : AppCompatActivity(), CacheListener {

    companion object {
        lateinit var lock: String
        var position: Int = 0
        var song_id: Long = 0
        lateinit var wlMedia: WlMedia
        lateinit var observer: Observer<Boolean>
        lateinit var observert: Observer<String>
        lateinit var observerset: Observer<Int>
        lateinit var observerno: Observer<Boolean>
        var bool: Boolean = false
        var id: Int = 0
        lateinit var adapter: PlaySongAdapter
        lateinit var t1: String
        lateinit var t2: String
        lateinit var m: String
        lateinit var m1: Bitmap
        lateinit var m2: Bitmap
        var playingMusicList: MutableList<Music>? = null
    }

    private var adaptert: PlayListAdapter? = null
    private var type: Int = 2
    var max: Long = 0
    private lateinit var sp: SharedPreferences
    private var min: Long = 0
    private var pos: Int = 0
    private var bitmap: Bitmap? = null
    private var playingMusic: Music? = null
    private var binder: MusicService.MyBinder? = null
    private var coverFragment = CoverFragment()
    private var lyricFragment = LyricFragment()
    private val fragments = mutableListOf<Fragment>()
    private lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_play)
        context = this
        detailView.animation = moveToViewLocation()
        fragments.add(coverFragment)
        fragments.add(lyricFragment)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        floatingActionButton.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#06b7ff"));
        initView()
        initData()
        sp = getSharedPreferences("User", Context.MODE_PRIVATE)

        val intent = Intent(this, LockService::class.java)
        startService(intent)
        //广播 添加广播的action

        val intentFilter = IntentFilter()
        intentFilter.addAction("del")
        intentFilter.addAction("pre")
        intentFilter.addAction("play")
        intentFilter.addAction("next")
        registerReceiver(broadcastReceiver, intentFilter)
    }

    var connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            binder = service as MusicService.MyBinder
            binder!!.startMusic()
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (Objects.requireNonNull(intent.action)) {
                "del" -> Notification.deleteNotification()
                "pre" ->
                    Observable.just(1).subscribe(observerset)
                "play" ->
                    if (playPauseIv.isPlaying) {
                        Observable.just(0).subscribe(observerset)
                    } else {
                        Observable.just(3).subscribe(observerset)
                    }
                "next" ->
                    Observable.just(2).subscribe(observerset)
                else -> {
                }
            }
        }
    }


    @SuppressLint("CheckResult", "ResourceAsColor")
    private fun initView() {
        playPauseIv.setOnClickListener {
            if (playPauseIv.isPlaying) {
                Observable.just(0).subscribe(observerset)
            } else {
                Observable.just(3).subscribe(observerset)
            }
        }

        RxView.clicks(top_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (bool) {
                    moveTaskToBack(true)
                    in_indel.visibility = View.GONE
                } else {
                    Observable.just(true).subscribe(observer)
                }
            }

        RxView.clicks(icon1)
            .throttleFirst(0, TimeUnit.SECONDS)
            .subscribe {
                if (type < 2) {
                    type++
                    if (type == 1) {
                        icon1.setImageResource(R.drawable.sui)

                        Toast.makeText(
                            context,
                            getText(R.string.sui),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        icon1.setImageResource(R.drawable.xun)
                        Toast.makeText(
                            context,
                            getText(R.string.lie),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    type = 0
                    icon1.setImageResource(R.drawable.dan)
                    Toast.makeText(
                        context,
                        getText(R.string.dan),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        RxView.clicks(icon2)
            .throttleFirst(0, TimeUnit.SECONDS)
            .subscribe {
                if (MusicApp.userlogin()) {
                    MaterialDialog.Builder(context)
                        .title("下载音乐")
                        .content("是否下载音乐")
                        .positiveText("确认")
                        .negativeText("取消")
                        .positiveColorRes(R.color.colorAccentDarkTheme)
                        .negativeColorRes(R.color.red)
                        .onPositive { _: MaterialDialog?, _: DialogAction? ->

                            val downs = mDownDao.querys(playingMusic!!.song_id)
                            if (downs.size > 0) {
                                for (its in downs) {
                                    if (its.type == 0) {
                                        val request = OkGo.get<File>(playingMusic!!.uri)
                                        OkDownload.request(playingMusic!!.uri, request) //
                                            .priority(0)
                                            .fileName("music" + playingMusic!!.song_id + ".mp3") //
                                            .save() //
                                            .register(
                                                LogDownloadListener(
                                                    playingMusic,
                                                    context,
                                                    0,
                                                    downs,
                                                    0
                                                )
                                            ) //
                                            .start()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            getText(R.string.download_carry),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                val request = OkGo.get<File>(playingMusic!!.uri)
                                OkDownload.request(playingMusic!!.uri, request) //
                                    .priority(0)
                                    .fileName("music" + playingMusic!!.song_id + ".mp3") //
                                    .save() //
                                    .register(
                                        LogDownloadListener(
                                            playingMusic,
                                            context,
                                            0,
                                            downs,
                                            1
                                        )
                                    ) //
                                    .start()
                            }
                        }
                        .show()
                } else {
                    MaterialDialog.Builder(context)
                        .title("登录")
                        .content("未登陆账号，是否登录")
                        .positiveText("确认")
                        .negativeText("取消")
                        .positiveColorRes(R.color.colorAccentDarkTheme)
                        .negativeColorRes(R.color.red)
                        .onPositive { _: MaterialDialog?, _: DialogAction? ->
                            val intent = Intent()
                            context.let { intent.setClass(it, LoginActivity().javaClass) }
                            startActivity(intent)
                        }
                        .show()
                }

            }

        RxView.clicks(icon3)
            .throttleFirst(0, TimeUnit.SECONDS)
            .subscribe {
                if (MusicApp.userlogin()) {
                    in_indel.visibility = View.VISIBLE
                    del.visibility = View.GONE
                    in_title.text = getText(R.string.song_but)
                    val list: MutableList<Playlist> =
                        mPlaylistDao.querys(sp.getString("userid", "").toString())
                    initSongList(list)

                } else {
                    MaterialDialog.Builder(context)
                        .title("登录")
                        .content("未登陆账号，是否登录")
                        .positiveText("确认")
                        .negativeText("取消")
                        .positiveColorRes(R.color.colorAccentDarkTheme)
                        .negativeColorRes(R.color.red)
                        .onPositive { _: MaterialDialog?, _: DialogAction? ->
                            val intent = Intent()
                            context.let { intent.setClass(it, LoginActivity().javaClass) }
                            startActivity(intent)
                        }
                        .show()
                }

            }

        RxView.clicks(icon4)
            .throttleFirst(0, TimeUnit.SECONDS)
            .subscribe {
                in_indel.visibility = View.VISIBLE
                del.setImageResource(R.drawable.list_del)
                in_title.text = ""
                initPlayList()
                if (id > 3) {
                    in_list.scrollToPosition(id - 3)
                } else {
                    in_list.scrollToPosition(id)
                }

            }

        RxView.clicks(list_dow)
            .throttleFirst(0, TimeUnit.SECONDS)
            .subscribe {
                in_indel.visibility = View.GONE
            }


        RxView.clicks(pre)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                Observable.just(1).subscribe(observerset)
            }
        RxView.clicks(next)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                Observable.just(2).subscribe(observerset)
            }

        RxView.clicks(del)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                MaterialDialog.Builder(context)
                    .title("播放列表")
                    .content("确定要清空播放列表")
                    .positiveText("确认")
                    .negativeText("取消")
                    .positiveColorRes(R.color.colorAccentDarkTheme)
                    .negativeColorRes(R.color.red)
                    .onPositive { _: MaterialDialog?, _: DialogAction? ->
                        Observable.just(true).subscribe(observer)
                    }
                    .show()


            }

        progressSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                position = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                wlMedia.seek(position.toDouble())
                wlMedia.seekTimeCallBack(false)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                wlMedia.seek(position.toDouble())
                wlMedia.seekTimeCallBack(true)
            }
        })
    }


    fun initData() {
        val bundle = intent.extras
        val album_id = bundle?.get("album_id") as Long
        val pos = bundle.get("pos") as Int
        val list = bundle.get("list") as String
        val types = bundle.get("type") as Int

        if (types == 1) {
            val obj: JsonArray = Gson().fromJson(list, JsonArray::class.java)
            val song: MutableList<Music> = Gson().fromJson(
                obj,
                object : TypeToken<MutableList<Music>>() {}.type
            )
            song.removeAt(0)
            if (song.isNotEmpty()) {

                id = pos
                MusicApp.setAblumid(album_id)
                playingMusicList = song
                playingMusic = song[pos]
                start(song[pos])

            }
        } else if (types == 2) {
            val obj: JsonArray = Gson().fromJson(list, JsonArray::class.java)
            val song: MutableList<Music> = Gson().fromJson(
                obj,
                object : TypeToken<MutableList<Music>>() {}.type
            )
            if (song.isNotEmpty()) {

                id = pos
                MusicApp.setAblumid(album_id)
                playingMusicList = song
                playingMusic = song[pos]
                start(song[pos])
            }
        }


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            try {
                val bundle = intent.extras
                val album_id = bundle?.get("album_id") as Long
                val pos = bundle.get("pos") as Int
                val list = bundle.get("list") as String
                val types = bundle.get("type") as Int

                if (types == 1) {
                    val obj: JsonArray = Gson().fromJson(list, JsonArray::class.java)
                    val song: MutableList<Music> = Gson().fromJson(
                        obj,
                        object : TypeToken<MutableList<Music>>() {}.type
                    )
                    song.removeAt(0)
                    if (song.isNotEmpty()) {

                        if (song[pos].song_id == song_id) {
                            playingMusicList = song
                        } else {
                            id = pos
                            MusicApp.setAblumid(album_id)
                            playingMusicList = song
                            playingMusic = song[pos]
                            starts(song[pos])
                        }

                    }
                } else if (types == 2) {
                    val obj: JsonArray = Gson().fromJson(list, JsonArray::class.java)
                    val song: MutableList<Music> = Gson().fromJson(
                        obj,
                        object : TypeToken<MutableList<Music>>() {}.type
                    )
                    if (song.isNotEmpty()) {

                        if (song[pos].song_id == song_id) {
                            playingMusicList = song
                        } else {
                            id = pos
                            MusicApp.setAblumid(album_id)
                            playingMusicList = song
                            playingMusic = song[pos]
                            starts(song[pos])
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun onResume() {
        super.onResume()

        //退出接口
        observer = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(boolean: Boolean) {
                if (boolean) {
                    wlMedia.exit()
                    playingMusicList!!.clear()
                    MusicApp.setPlay(false)
                    MusicApp.setAblumid(0)
                    MusicApp.setMusic(playingMusicList)
                    bool = false
                    finish()
                    in_indel.visibility = View.GONE
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        //搜索页面歌曲播放接口
        observert = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(uri: String) {
                println(Dencry.dencryptString(uri))
                if (bool) {
                    musicplay(1, Dencry.dencryptString(uri))
                } else {
                    musicplay(0, Dencry.dencryptString(uri))
                }
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        //通知栏接口
        observerno = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(bool: Boolean) {
                if (bool) {
                    Notification.init(context, t1, t2, m1, 1)
                } else {
                    Notification.init(context, t1, t2, m1, 0)
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }


        //播放、上一首、下一首
        observerset = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: Int) {
                try {
                    when (data) {
                        0 -> {
                            if (bool) {
                                if (playPauseIv.isPlaying) {
                                    playPauseIv.pause()
                                    wlMedia.pause()
                                    MusicApp.setPlay(false)
                                    Observable.just(false).subscribe(observerno)
                                    coverFragment.stopRotateAnimation()

                                }
                            }
                        }
                        1 -> {
                            playtype(1)
                        }
                        2 -> {
                            playtype(2)
                        }
                        3 -> {
                            if (bool) {
                                if (!playPauseIv.isPlaying) {
                                    playPauseIv.play()
                                    wlMedia.resume()
                                    MusicApp.setPlay(true)
                                    Observable.just(true).subscribe(observerno)
                                    coverFragment.resumeRotateAnimation()

                                }
                            }

                        }
                    }
                } catch (e: Exception) {
                }


            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }
    }

    override fun onStop() {
        super.onStop()

    }



    /**
     * 初始化歌曲
     */
    private fun initPlayList() {
        in_list.layoutManager = LinearLayoutManager(context)
        in_list.itemAnimator = DefaultItemAnimator()
        adaptert = playingMusicList?.let { PlayListAdapter(it, context) }
        in_list.adapter = adaptert
        adaptert!!.setOnItemClickListener(object : PlayListAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (id != position) {
                    id = position
                    adaptert!!.notifyDataSetChanged()
                    starts(playingMusicList!![position])
                }


            }
        })

    }

    /**
     * 初始化歌曲
     */
    private fun initSongList(song: MutableList<Playlist>) {
        in_list.layoutManager = LinearLayoutManager(context)
        in_list.itemAnimator = DefaultItemAnimator()
        adapter = PlaySongAdapter(song, context)
        in_list.adapter = adapter
        adapter.setOnItemClickListener(object : PlaySongAdapter.ItemClickListener {
            @SuppressLint("ResourceAsColor")
            override fun onItemClick(view: View, position: Int) {
                pos = position
                MaterialDialog.Builder(context)
                    .title("添加音乐")
                    .content("是否将音乐加入此歌单")
                    .positiveText("确认")
                    .negativeText("取消")
                    .positiveColorRes(R.color.colorAccentDarkTheme)
                    .negativeColorRes(R.color.red)
                    .onPositive { _: MaterialDialog?, _: DialogAction? ->
                        val idmap = mutableListOf<Music>()
                        playingMusic?.let { idmap.add(it) }
                        val playlist: Playlist = mPlaylistDao.query(song[position].play_list_id)[0]
                        val playsong = mDownDao.query(song[position].play_list_id)
                        val songs = mutableListOf<Music>()
                        songs.addAll(idmap)
                        if (playsong.size > 0) {
                            if (idmap.size > 0) {
                                for (sea in idmap) {
                                    for (det in playsong) {
                                        if (sea.song_id == det.song_id) {
                                            songs.remove(sea)
                                        }
                                    }
                                }
                                if (songs.size > 0) {
                                    val num = (playlist.song_num.toInt() + songs.size).toString()
                                    MusicPlayModel.addSong(
                                        context,
                                        songs,
                                        num,
                                        song[position].play_list_id, 0, position
                                    )
                                } else {
                                    Toast.makeText(
                                        context,
                                        getText(R.string.play_mode),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }

                        } else {
                            if (songs.size > 0) {
                                val num = (playlist.song_num.toInt() + songs.size).toString()
                                MusicPlayModel.addSong(
                                    context,
                                    songs,
                                    num,
                                    song[position].play_list_id, 0, position
                                )
                                adapter.update(position, num)
                            } else {
                                Toast.makeText(
                                    context,
                                    getText(R.string.play_mode),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    .show()

            }
        })
    }

    private fun start(music: Music) {
        try {

            playingMusic = music
            //更新标题
            titleIv.text = music.name
            song_id = music.song_id
            MusicApp.setPosition(id)
            MusicApp.setMusic(playingMusicList)

            val artist = music.all_artist
            var srtist_name = ""
            for (it in artist) {
                if (srtist_name != "") {
                    srtist_name += "/" + it.name
                } else {
                    srtist_name = it.name
                }

            }
            subTitleTv.text = srtist_name
            Ablemname.text = music.album_name

            if (music.uri != "") {
                musicplay(0, music.uri)
                lyricFragment.lrcView(music.song_id)

            } else {
                MusicPlayModel.musicpath(
                    "tencent", music.publish_time, "hq",
                    Cookie.getCookie()
                )
            }

            coverFragment.setImageBitmap(music.pic_url)
            t1 = music.name
            t2 = srtist_name
            m = music.pic_url

            object : Thread() {
                override fun run() {
                    bitmap = BitmapUtils.netUrlPicToBmps(music.pic_url)
                    if (bitmap != null) {
                        m1 = bitmap!!
                        Observable.just(true).subscribe(observerno)
                    }
                }
            }.start()

            object : Thread() {
                override fun run() {
                    val bitmaps = BitmapUtils.netUrlPicToBmps(music.pic_url)
                    if (bitmaps != null) {
                        BlurKit.getInstance().blur(bitmaps, 25)
                        m2 = bitmaps
                    }
                }
            }.start()


        } catch (e: Exception) {
        }

    }

    private fun starts(music: Music) {
        try {

            playPauseIv.pause()
            coverFragment.stopRotateAnimation()
            if (wlMedia.isPlaying) {
                wlMedia.stop()
            }

            MusicApp.setPosition(id)
            MusicApp.setMusic(playingMusicList)

            song_id = music.song_id
            playingMusic = music

            //更新标题
            titleIv.text = music.name
            val artist = music.all_artist
            var srtist_name = ""
            for (it in artist) {
                if (srtist_name != "") {
                    srtist_name += "/" + it.name
                } else {
                    srtist_name = it.name
                }

            }
            subTitleTv.text = srtist_name
            Ablemname.text = music.album_name

            if (music.uri != "") {
                musicplay(1, music.uri)
                lyricFragment.lrcView(music.song_id)
            } else {
                MusicPlayModel.musicpath(
                    "tencent", music.publish_time, "hq",
                    Cookie.getCookie()
                )
            }

            coverFragment.setImageBitmap(music.pic_url)
            t1 = music.name
            t2 = srtist_name
            m = music.pic_url

            object : Thread() {
                override fun run() {
                    bitmap = BitmapUtils.netUrlPicToBmps(music.pic_url)
                    if (bitmap != null) {
                        m1 = bitmap!!
                        Observable.just(true).subscribe(observerno)
                    }

                }
            }.start()

            object : Thread() {
                override fun run() {
                    val bitmaps = BitmapUtils.netUrlPicToBmps(music.pic_url)
                    if (bitmaps != null) {
                        BlurKit.getInstance().blur(bitmaps, 25)
                        m2 = bitmaps
                    }

                }
            }.start()

        } catch (e: Exception) {
        }


    }


    fun musicplay(type: Int, uri: String) {
        val proxy: HttpProxyCacheServer = getProxy()
        val proxyUrl: String = proxy.getProxyUrl(uri)
        if (type == 0) {
            play(proxyUrl)
        } else {
            wlMedia.source = proxyUrl
            wlMedia.next()
        }


    }

    private fun getProxy(): HttpProxyCacheServer {
        return MusicApp.getProxy(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        Observable.just(true).subscribe(observer)
        Notification.deleteNotification()
        unregisterReceiver(broadcastReceiver) // 注销广播
    }


    @SuppressLint("SetTextI18n")
    fun play(uri: String?) {

        try {
            wlMedia = WlMedia()
            wlMedia.setPlayModel(WlPlayModel.PLAYMODEL_ONLY_AUDIO) //设置只播放音频（必须）
            wlMedia.setSampleRate(WlSampleRate.SAMPLE_RATE_48000)
            wlMedia.source = uri //设置数据源

            wlMedia.setOnPreparedListener {
                if (wlMedia.duration > 0) {
                    Observable.just((wlMedia.duration).toLong())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<Long> {
                            override fun onSubscribe(d: Disposable) {}
                            override fun onNext(aLong: Long) {
                                max = aLong
                                progressSb.max = aLong.toInt()
                                val f = aLong / 60
                                val m = aLong % 60
                                progressTv.text = "00:00"
                                wlMedia.seek(0.0)
                                durationTv.text =
                                    unitFormat(f.toInt()) + ":" + unitFormat(m.toInt())
                                lrcView.updateTime(0)
                                coverFragment.startRotateAnimation(wlMedia.isPlaying)
                            }

                            override fun onError(e: Throwable) {}
                            override fun onComplete() {
                                bool = true
                                playPauseIv.play()
                                wlMedia.start() //准备完成开始播放
                                MusicApp.setPlay(true)

                            }
                        })
                } else {
                    if (playingMusicList!!.size - 1 == id) {
                        id = 0
                        starts(playingMusicList!![0])
                    } else {
                        id += 1
                        starts(playingMusicList!![id])
                    }
                }

            }
            wlMedia.setOnTimeInfoListener { currentTime, bufferTime ->

                min = currentTime.toLong()
                val fs = min / 60
                val ms = min % 60
                progressTv.text = unitFormat(fs.toInt()) + ":" + unitFormat(ms.toInt())
                progressSb.progress = min.toInt()
                lrcView.updateTime(min * 1000)
            }

            wlMedia.setOnLoadListener { b ->
                if (b) {
                    playPauseIv.setLoading(true)
                    WlLog.d("加载中")
                } else {
                    WlLog.d("加载完成")
                    playPauseIv.setLoading(false)
                }
            }

            wlMedia.setOnErrorListener { _, _ ->
                Toast.makeText(
                    context,
                    getText(R.string.error_playing_track),
                    Toast.LENGTH_LONG
                ).show()
            }

            wlMedia.setOnCompleteListener { type ->
               //
                when {
                    type === WlComplete.WL_COMPLETE_EOF -> {
                        WlLog.d("正常播放结束")
                        playPauseIv.setLoading(false)
                        Observable.just(2).subscribe(observerset)
                    }
                    type === WlComplete.WL_COMPLETE_NEXT -> {
                        WlLog.d("切换下一首，导致当前结束")
                        playPauseIv.setLoading(false)
                    }
                    type === WlComplete.WL_COMPLETE_HANDLE -> {
                        WlLog.d("手动结束")
                        playPauseIv.setLoading(false)
                        Observable.just(2).subscribe(observerset)
                    }
                    type === WlComplete.WL_COMPLETE_ERROR -> {
                        WlLog.d("播放出现错误结束")
                        playPauseIv.setLoading(false)
                        Observable.just(2).subscribe(observerset)

                    }
                }
            }

            wlMedia.prepared()

        } catch (e: Exception) {
            Toast.makeText(
                context,
                getText(R.string.error_playing_track),
                Toast.LENGTH_LONG
            ).show()
        }


    }

    fun unitFormat(time: Int): String {
        return if (time in 0..9)
            "0$time"
        else
            "" + time
    }


    fun playtype(des: Int) {
        when (type) {
            0 -> {
                //单曲循环
                starts(playingMusic!!)
            }
            1 -> {
                //随机播放
                val randoms = (0 until playingMusicList!!.size).random()
                id = randoms
                if (adaptert != null) {
                    adaptert!!.notifyItemChanged(id)
                }

                starts(playingMusicList!![randoms])
            }
            2 -> {
                //列表循环
                if (des == 1) {
                    if (id == 0) {
                        id = playingMusicList!!.size - 1
                        starts(playingMusicList!![id])
                        if (adaptert != null) {
                            adaptert!!.notifyItemChanged(id)
                        }
                    } else {
                        id -= 1
                        starts(playingMusicList!![id])
                        if (adaptert != null) {
                            adaptert!!.notifyItemChanged(id)
                        }

                    }

                } else if(des==2){
                    if (playingMusicList!!.size - 1 == id) {
                        id = 0
                        starts(playingMusicList!![0])
                        if (adaptert != null) {
                            adaptert!!.notifyItemChanged(id)
                        }
                    } else {
                        id += 1
                        starts(playingMusicList!![id])
                        if (adaptert != null) {
                            adaptert!!.notifyItemChanged(id)
                        }
                    }

                }


            }
        }
    }


    /**
     * 底部上移动画效果
     */
    private fun moveToViewLocation(): TranslateAnimation {
        val mHiddenAction = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_SELF,
            1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        )
        mHiddenAction.duration = 300
        return mHiddenAction
    }

    override fun onBackPressed() {
        if (bool) {
            moveTaskToBack(true)
            in_indel.visibility = View.GONE
        } else {
            Observable.just(true).subscribe(observer)
        }


    }

    override fun onCacheAvailable(cacheFile: File?, url: String?, percentsAvailable: Int) {
    }


}

