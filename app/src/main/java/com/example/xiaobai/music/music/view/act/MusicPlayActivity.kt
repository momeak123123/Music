package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.*
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.xiaobai.music.LockActivity
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.PlayListAdapter
import com.example.xiaobai.music.adapter.PlaySongAdapter
import com.example.xiaobai.music.adapter.ViewPagerAdapter
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.config.LogDownloadListener
import com.example.xiaobai.music.config.Notification
import com.example.xiaobai.music.music.model.MusicPlayModel
import com.example.xiaobai.music.music.view.fragment.CoverFragment
import com.example.xiaobai.music.music.view.fragment.LyricFragment
import com.example.xiaobai.music.service.LockService
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
import com.ywl5320.wlmedia.enums.WlPlayModel
import com.ywl5320.wlmedia.enums.WlSourceType
import io.alterac.blurkit.BlurKit
import io.reactivex.Flowable
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

class MusicPlayActivity : AppCompatActivity() {

    companion object {
        lateinit var lock: String
        var position: Int = 0
        var song_id: Long = 0
        lateinit var wlMedia: WlMedia
        lateinit var observer: Observer<Boolean>
        lateinit var observers: Observer<Long>
        lateinit var observerplay: Observer<MutableList<Music>>
        lateinit var observerset: Observer<Int>
        lateinit var observerno: Observer<Boolean>
        var bool: Boolean = false
        var id: Int = 0
        lateinit var adapter: PlaySongAdapter
        lateinit var t1: String
        lateinit var t2: String
        lateinit var m1: Bitmap
        lateinit var m2: Bitmap
        var playingMusicList: MutableList<Music>? = null
    }

    private var adaptert: PlayListAdapter? = null
    private var type: Int = 2
    lateinit var mDisposable: Disposable
    var max: Long = 0
    private lateinit var sp: SharedPreferences
    private var min: Long = 0
    private var pos: Int = 0
    private var bitmap: Bitmap? = null
    private var playingMusic: Music? = null

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
        MusicApp.setIsapp(true)
        lock = "1"
    }


    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (Objects.requireNonNull(intent.action)) {
                "del" -> Notification.deleteNotification()
                "pre" ->
                    if (bool) {
                        Observable.just(1).subscribe(observerset)
                    } else {

                        Toast.makeText(
                            context,
                            getText(R.string.secret_num),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                "play" ->
                    if (bool) {
                        if (playPauseIv.isPlaying) {
                            Observable.just(0).subscribe(observerset)
                        } else {
                            Observable.just(3).subscribe(observerset)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            getText(R.string.secret_num),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                "next" ->
                    if (bool) {
                        Observable.just(2).subscribe(observerset)
                    } else {
                        Toast.makeText(
                            context,
                            getText(R.string.secret_num),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                else -> {
                }
            }
        }
    }


    @SuppressLint("CheckResult", "ResourceAsColor")
    private fun initView() {
        playPauseIv.setOnClickListener {
            if (bool) {
                if (playPauseIv.isPlaying) {
                    Observable.just(0).subscribe(observerset)
                } else {
                    Observable.just(3).subscribe(observerset)
                }
            } else {
                Toast.makeText(
                    context,
                    getText(R.string.secret_num),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        RxView.clicks(top_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                moveTaskToBack(true)
                in_indel.visibility = View.GONE
                MusicApp.setIsapp(false)
            }

        RxView.clicks(icon1)
            .throttleFirst(0, TimeUnit.SECONDS)
            .subscribe {
                if (type < 2) {
                    type++
                    if (type == 1) {
                        Glide.with(context).load(R.drawable.sui).into(icon1)
                        Toast.makeText(
                            context,
                            getText(R.string.sui),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Glide.with(context).load(R.drawable.xun).into(icon1)
                        Toast.makeText(
                            context,
                            getText(R.string.lie),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    type = 0
                    Glide.with(context).load(R.drawable.dan).into(icon1)
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
                if (sp.getBoolean("login", false)) {
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
                if (sp.getBoolean("login", false)) {
                    in_indel.visibility = View.VISIBLE
                    Glide.with(context).load("").into(del)
                    in_title.text = getText(R.string.song_but)
                    val list: MutableList<Playlist> = mPlaylistDao.queryAll()
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
                Glide.with(this).load(R.drawable.list_del).placeholder(R.color.main_black_grey)
                    .into(del)
                in_title.text = ""
                initPlayList()
            }

        RxView.clicks(list_dow)
            .throttleFirst(0, TimeUnit.SECONDS)
            .subscribe {
                in_indel.visibility = View.GONE
            }


        RxView.clicks(pre)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (bool) {
                    Observable.just(1).subscribe(observerset)
                } else {

                    Toast.makeText(
                        context,
                        getText(R.string.secret_num),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        RxView.clicks(next)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (bool) {
                    Observable.just(2).subscribe(observerset)
                } else {
                    Toast.makeText(
                        context,
                        getText(R.string.secret_num),
                        Toast.LENGTH_SHORT
                    ).show()
                }

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
                Observable.just(position.toLong()).subscribe(observers)
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
                song_id = song[pos].song_id
                MusicApp.setAblumid(album_id)
                playingMusicList = song
                playingMusic = song[pos]
                start(playingMusic!!)

            }
        } else if (types == 2) {
            val obj: JsonArray = Gson().fromJson(list, JsonArray::class.java)
            val song: MutableList<Music> = Gson().fromJson(
                obj,
                object : TypeToken<MutableList<Music>>() {}.type
            )
            if (song.isNotEmpty()) {

                id = pos
                song_id = song[pos].song_id
                MusicApp.setAblumid(album_id)
                playingMusicList = song
                playingMusic = song[pos]
                start(playingMusic!!)
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
                            song_id = song[pos].song_id
                            MusicApp.setAblumid(album_id)
                            playingMusicList = song
                            playingMusic = song[pos]
                            starts(playingMusic!!)
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
                            song_id = song[pos].song_id
                            MusicApp.setAblumid(album_id)
                            playingMusicList = song
                            playingMusic = song[pos]
                            starts(playingMusic!!)
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun onResume() {
        super.onResume()

        observer = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(boolean: Boolean) {
                if(boolean){
                    if (wlMedia.isPlaying) {
                        wlMedia.exit()
                    }
                    playingMusicList!!.clear()
                    MusicApp.setPlay(false)
                    MusicApp.setAblumid(0)
                    MusicApp.setMusic(playingMusicList)
                    bool = false
                    moveTaskToBack(true)
                    in_indel.visibility = View.GONE
                    MusicApp.setIsapp(false)
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        observers = object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(bool: Long) {
                if (!mDisposable.isDisposed) {
                    mDisposable.dispose()
                    time(bool, max - bool)
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

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

        observerplay = object : Observer<MutableList<Music>> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(song: MutableList<Music>) {
                song.removeAt(0)
                if (song.isNotEmpty()) {
                    id = 0
                    song_id = song[0].song_id
                    playingMusicList = song
                    playingMusic = song[0]
                    start(playingMusic!!)
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        observerset = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: Int) {
                try {
                    when (data) {
                        0 -> {
                            if (playPauseIv.isPlaying) {
                                playPauseIv.pause()
                                wlMedia.pause()
                                mDisposable.dispose()
                                MusicApp.setPlay(false)
                                Observable.just(false).subscribe(observerno)
                                coverFragment.stopRotateAnimation()
                                AlbumDetActivity.adapter.notifyItemChanged(0)
                                SongDetActivity.adapter.notifyItemChanged(0)
                                DownloadActivity.adapter.notifyItemChanged(0)
                            }
                        }
                        1 -> {
                            if (id == 0) {
                                id = playingMusicList!!.size - 1
                                starts(playingMusicList!![id])
                            } else {
                                id -= 1
                                starts(playingMusicList!![id])
                            }

                        }
                        2 -> {
                            if (playingMusicList!!.size - 1 == id) {
                                id = 0
                                starts(playingMusicList!![0])
                            } else {
                                id += 1
                                starts(playingMusicList!![id])
                            }
                        }
                        3 -> {
                            if (!playPauseIv.isPlaying) {
                                playPauseIv.play()
                                wlMedia.resume()
                                MusicApp.setPlay(true)
                                Observable.just(true).subscribe(observerno)
                                Observable.just(position.toLong()).subscribe(observers)
                                coverFragment.resumeRotateAnimation()
                                AlbumDetActivity.adapter.notifyItemChanged(0)
                                SongDetActivity.adapter.notifyItemChanged(0)
                                DownloadActivity.adapter.notifyItemChanged(0)
                            }

                        }
                    }
                }catch (e:Exception){}


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
                if(id!=position){
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
            playPauseIv.setLoading(true)
            playingMusic = music
            //更新标题
            titleIv.text = music.name
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
            object : Thread() {
                override fun run() {
                    bitmap = BitmapUtils.netUrlPicToBmp(music.pic_url)
                    coverFragment.setImageBitmap(bitmap)
                    t1 = music.name
                    t2 = srtist_name
                    m1 = bitmap!!
                    Observable.just(true).subscribe(observerno)
                }
            }.start()

            object : Thread() {
                override fun run() {
                    val bitmaps = BitmapUtils.netUrlPicToBmp(music.pic_url)
                    BlurKit.getInstance().blur(bitmaps, 25)
                    m2 = bitmaps!!

                }
            }.start()

            play(music.uri)
            lyricFragment.lrcView(music.song_id)
        } catch (e: Exception) {
        }

    }

    private fun starts(music: Music) {
        try {

            playPauseIv.pause()
            coverFragment.stopRotateAnimation()
            MusicApp.setPlay(true)
            wlMedia.stop()
            mDisposable.dispose()
            wlMedia.seek(0.00)
            wlMedia.seekTimeCallBack(false)
            Observable.just(0L).subscribe(observers)
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

            object : Thread() {
                override fun run() {

                    bitmap = BitmapUtils.netUrlPicToBmp(music.pic_url)
                    coverFragment.setImageBitmap(bitmap)
                    t1 = music.name
                    t2 = srtist_name
                    m1 = bitmap!!
                    Observable.just(true).subscribe(observerno)
                }
            }.start()

            object : Thread() {
                override fun run() {
                    val bitmaps = BitmapUtils.netUrlPicToBmp(music.pic_url)
                    BlurKit.getInstance().blur(bitmaps, 25)
                    m2 = bitmaps!!

                }
            }.start()
            playPauseIv.setLoading(true)
            wlMedia.source = music.uri
            wlMedia.next()
            lyricFragment.lrcView(music.song_id)
        } catch (e: Exception) {
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        if (wlMedia.isPlaying) {
            wlMedia.exit()
        }
        Notification.deleteNotification()
        unregisterReceiver(broadcastReceiver) // 注销广播
    }


    @SuppressLint("SetTextI18n")
    fun play(uri: String?) {
        wlMedia = WlMedia()
        wlMedia.setPlayModel(WlPlayModel.PLAYMODEL_ONLY_AUDIO) //设置只播放音频（必须）
        wlMedia.setSourceType(WlSourceType.NORMAL) //url源模式
        wlMedia.source = uri //设置数据源

        wlMedia.setOnPreparedListener {
            if (wlMedia.duration > 0) {
                Observable.just((wlMedia.duration).toLong())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Long> {
                        override fun onSubscribe(d: Disposable) {}
                        override fun onNext(aLong: Long) {
                            max = aLong
                            bool = true
                            progressSb.max = aLong.toInt()
                            val f = aLong / 60
                            val m = aLong % 60
                            progressTv.text = "00:00"
                            durationTv.text = unitFormat(f.toInt()) + ":" + unitFormat(m.toInt())
                            lrcView.updateTime(0)
                            playPauseIv.setLoading(false)

                        }

                        override fun onError(e: Throwable) {}
                        override fun onComplete() {
                            playPauseIv.play()
                            wlMedia.start() //准备完成开始播放
                            MusicApp.setPlay(true)
                            coverFragment.startRotateAnimation(wlMedia.isPlaying)
                            time(0, max)
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

        wlMedia.setOnErrorListener { code, msg ->
            Log.d("ywl5320", "code :$code, msg :$msg")
        }

        wlMedia.setOnLoadListener { load ->
            Log.d("ywl5320", "load --> $load")
        }

        wlMedia.setOnCompleteListener {
            Log.d("ywl5320", "complete")
        }

        wlMedia.prepared()


    }

    @SuppressLint("SetTextI18n")
    fun time(init: Long, count: Long) {
        mDisposable = Flowable.intervalRange(init, count + 1, 0, 1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { t ->
                min = t
                val fs = t / 60
                val ms = t % 60
                progressTv.text = unitFormat(fs.toInt()) + ":" + unitFormat(ms.toInt())
                progressSb.progress = t.toInt()
                lrcView.updateTime(t * 1000)
            }
            .doOnComplete {
                playPauseIv.pause()
                coverFragment.stopRotateAnimation()
                playtype()
            }
            .subscribe()
    }

    fun unitFormat(time: Int): String {
        return if (time in 0..9)
            "0$time"
        else
            "" + time
    }


    fun playtype() {
        when (type) {
            0 -> {
                //单曲循环
                starts(playingMusic!!)
            }
            1 -> {
                //随机播放
                val randoms = (0 until playingMusicList!!.size).random()
                id = randoms
                adaptert!!.notifyItemChanged(id)
                starts(playingMusicList!![randoms])
            }
            2 -> {
                //列表循环

                if (playingMusicList!!.size - 1 == id) {
                    id = 0
                    starts(playingMusicList!![0])
                    adaptert!!.notifyItemChanged(id)
                } else {
                    id += 1
                    starts(playingMusicList!![id])
                    adaptert!!.notifyItemChanged(id)
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
        moveTaskToBack(true)
        in_indel.visibility = View.GONE
        MusicApp.setIsapp(false)
    }

}
