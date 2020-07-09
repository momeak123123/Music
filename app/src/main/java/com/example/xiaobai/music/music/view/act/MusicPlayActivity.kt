package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
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
import io.alterac.blurkit.BlurKit
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.frag_player_lrcview.*
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.music_play.*
import kotlinx.android.synthetic.main.play_list.*
import java.io.File
import java.util.concurrent.TimeUnit

class MusicPlayActivity : AppCompatActivity() {

    companion object {
        var position: Int = 0
        var song_id: Long = 0
        var bool: Boolean = false
        var id: Int = 0
        lateinit var observer: Observer<Boolean>
        lateinit var observers: Observer<Boolean>
        lateinit var observerui: Observer<Long>
        lateinit var observert: Observer<String>
        lateinit var observerset: Observer<Int>
        lateinit var observerseek: Observer<Double>
        lateinit var observerseeks: Observer<Long>
        lateinit var observerplay: Observer<Int>
        lateinit var adapter: PlaySongAdapter
        lateinit var t1: String
        lateinit var t2: String
        lateinit var m: String
        lateinit var m1: Bitmap
        lateinit var m2: Bitmap
        lateinit var playingMusicList: MutableList<Music>
    }

    private var adaptert: PlayListAdapter? = null
    private var count: Int = 2
    var max: Long = 0
    private var search: Boolean = false
    private lateinit var sp: SharedPreferences
    private var min: Long = 0
    private var pos: Int = 0
    private lateinit var playingMusic: Music
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

        sp = getSharedPreferences("User", Context.MODE_PRIVATE)

        initKeyguardManager()

        initView()
        initData()

    }

    private fun initKeyguardManager() {
        val keyguardManager =
            applicationContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val keyguardLock = keyguardManager.newKeyguardLock("")
        keyguardLock.disableKeyguard() //取消系统锁屏

        val intent = Intent(this, LockService::class.java)
        startService(intent)
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
                finish()
            }

        RxView.clicks(icon1)
            .throttleFirst(0, TimeUnit.SECONDS)
            .subscribe {
                if (count < 2) {
                    count++
                    if (count == 1) {
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
                    count = 0
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

                            val downs = mDownDao.querys(playingMusic.song_id)
                            if (downs.size > 0) {
                                for (its in downs) {
                                    if (its.type == 0) {
                                        val request = OkGo.get<File>(playingMusic.uri)
                                        OkDownload.request(playingMusic.uri, request) //
                                            .priority(0)
                                            .fileName("music" + playingMusic.song_id + ".mp3") //
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
                                val request = OkGo.get<File>(playingMusic.uri)
                                OkDownload.request(playingMusic.uri, request) //
                                    .priority(0)
                                    .fileName("music" + playingMusic.song_id + ".mp3") //
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
                    if (!search) {
                        in_indel.visibility = View.VISIBLE
                        del.visibility = View.GONE
                        in_title.text = getText(R.string.song_but)
                        val list: MutableList<Playlist> =
                            mPlaylistDao.querys(sp.getString("userid", "").toString())
                        initSongList(list)
                    } else {
                        Toast.makeText(
                            context,
                            getText(R.string.error_searcher),
                            Toast.LENGTH_LONG
                        ).show()
                    }


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
                musicplay(5, position.toDouble(), id)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                musicplay(6, position.toDouble(), id)
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
                if (song[pos].song_id == song_id && pos == id) {
                    musicplay(7, 0.0, pos)
                } else {
                    MusicApp.setAblumid(album_id)
                    MusicApp.setMusic(song)
                    playingMusicList = song
                    MusicApp.setPosition(pos)
                    musicplay(0, 0.0, pos)
                }

            }
        } else if (types == 2) {
            val obj: JsonArray = Gson().fromJson(list, JsonArray::class.java)
            val song: MutableList<Music> = Gson().fromJson(
                obj,
                object : TypeToken<MutableList<Music>>() {}.type
            )
            if (song.isNotEmpty()) {
                if (song[pos].song_id == song_id && pos == id) {
                    musicplay(7, 0.0, pos)
                } else {
                    MusicApp.setAblumid(album_id)
                    MusicApp.setMusic(song)
                    playingMusicList = song
                    MusicApp.setPosition(pos)
                    musicplay(0, 0.0, pos)
                }
            }
        } else if (types == 0) {
            musicplay(7, 0.0, pos)
        }


    }

    fun del() {
        val intentservice = Intent(this, MusicService::class.java)
        stopService(intentservice)
        finish()
    }

    override fun onResume() {
        super.onResume()

        //退出接口
        observer = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(boolean: Boolean) {
                if (boolean) {
                    del()
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        //加载页面
        observers = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(boolean: Boolean) {
                if (boolean) {

                    id = MusicApp.getPosition()
                    playingMusic = playingMusicList[id]
                    song_id = playingMusic.song_id

                    //更新标题
                    titleIv.text = playingMusic.name
                    val artist = playingMusic.all_artist
                    var srtist_name = ""
                    for (it in artist) {
                        if (srtist_name != "") {
                            srtist_name += "/" + it.name
                        } else {
                            srtist_name = it.name
                        }

                    }
                    subTitleTv.text = srtist_name
                    Ablemname.text = playingMusic.album_name
                    playPauseIv.setLoading(true)
                    coverFragment.setImagePath(playingMusic.pic_url)
                    t1 = playingMusic.name
                    t2 = srtist_name
                    m = playingMusic.pic_url
                    lyricFragment.lrcView(playingMusic.song_id)

                    object : Thread() {
                        override fun run() {
                            val bitmap = BitmapUtils.netUrlPicToBmp(playingMusic.pic_url)
                            if (bitmap != null) {
                                m1 = bitmap
                                Notification.init(t1, t2, m1,1)
                            }

                        }
                    }.start()

                    object : Thread() {
                        override fun run() {
                            val bitmaps = BitmapUtils.netUrlPicToBmp(playingMusic.pic_url)
                            if (bitmaps != null) {
                                BlurKit.getInstance().blur(bitmaps, 25)
                                m2 = bitmaps
                            }

                        }
                    }.start()


                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        //更新ui
        observerui = object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {}

            @SuppressLint("SetTextI18n")
            override fun onNext(duration: Long) {

                coverFragment.setImagePath(playingMusic.pic_url)

                max = duration
                progressSb.max = duration.toInt()
                val f = duration / 60
                val m = duration % 60
                durationTv.text =
                    unitFormat(f.toInt()) + ":" + unitFormat(m.toInt())

                min = MusicApp.getPress().toLong()
                val fs = min / 60
                val ms = min % 60
                progressTv.text = unitFormat(fs.toInt()) + ":" + unitFormat(ms.toInt())
                progressSb.progress = min.toInt()
                lrcView.updateTime(min * 1000)

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
                bool = true
                playPauseIv.play()
                MusicApp.setPlay(true)
                coverFragment.startRotateAnimation(true)
            }

        }

        //搜索页面歌曲播放接口
        observert = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(uri: String) {
                musicplay(1, 0.0, id)
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
                            musicplay(3, 0.0, id)
                        }
                        1 -> {
                            musicplay(1, 0.0, id)
                        }
                        2 -> {
                            musicplay(2, 0.0, id)
                        }
                        3 -> {
                            musicplay(4, 0.0, id)
                        }
                    }
                } catch (e: Exception) {
                }


            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }


        //进度更新接口
        observerseek = object : Observer<Double> {
            override fun onSubscribe(d: Disposable) {}

            @SuppressLint("SetTextI18n")
            override fun onNext(bool: Double) {
                min = bool.toLong()
                val fs = min / 60
                val ms = min % 60
                progressTv.text = unitFormat(fs.toInt()) + ":" + unitFormat(ms.toInt())
                progressSb.progress = min.toInt()
                lrcView.updateTime(min * 1000)

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        observerseeks = object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {}

            @SuppressLint("SetTextI18n")
            override fun onNext(bool: Long) {
                musicplay(6, (bool / 1000).toDouble(), id)

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }


        //播放设置接口
        observerplay = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(bool: Int) {
                when (bool) {
                    0 -> {
                        playPauseIv.setLoading(true)
                    }
                    1 -> {
                        playPauseIv.setLoading(false)
                    }
                    2 -> {
                        progressSb.progress=0
                        playPauseIv.setLoading(false)
                        Toast.makeText(
                            context,
                            getText(R.string.error_playing_track),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    3 -> {
                        if (playPauseIv.isPlaying) {
                            playPauseIv.pause()
                            MusicApp.setPlay(false)
                            coverFragment.stopRotateAnimation()
                        }
                    }
                    4 -> {
                        if (!playPauseIv.isPlaying) {
                            playPauseIv.play()
                            MusicApp.setPlay(true)
                            coverFragment.resumeRotateAnimation()
                        }
                    }

                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }
    }

    fun unitFormat(time: Int): String {
        return if (time in 0..9)
            "0$time"
        else
            "" + time
    }

    override fun onStop() {
        super.onStop()

    }


    /**
     * 初始化歌曲列表
     */
    private fun initPlayList() {
        in_list.layoutManager = LinearLayoutManager(context)
        in_list.itemAnimator = DefaultItemAnimator()
        adaptert = PlayListAdapter(playingMusicList, context)
        in_list.adapter = adaptert
        adaptert!!.setOnItemClickListener(object : PlayListAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (id != position) {
                    id = position
                    adaptert!!.notifyDataSetChanged()
                    musicplay(0, 0.0, position)
                }
            }
        })

    }

    /**
     * 初始化专辑列表
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


    fun musicplay(type: Int, seek: Double, id: Int) {

        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("type", type)
        intent.putExtra("seek", seek)
        intent.putExtra("count", count)
        intent.putExtra("id", id)
        startService(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
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
        finish()
    }

}

