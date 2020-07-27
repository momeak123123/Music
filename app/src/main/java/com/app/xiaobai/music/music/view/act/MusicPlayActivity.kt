package com.app.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.xiaobai.music.LockActivity
import com.app.xiaobai.music.MusicApp
import com.app.xiaobai.music.R
import com.app.xiaobai.music.adapter.PlayListAdapter
import com.app.xiaobai.music.adapter.PlaySongAdapter
import com.app.xiaobai.music.adapter.ViewPagerAdapter
import com.app.xiaobai.music.bean.Music
import com.app.xiaobai.music.config.Constants
import com.app.xiaobai.music.config.LogDownloadListener
import com.app.xiaobai.music.config.Notifications
import com.app.xiaobai.music.music.model.MusicPlayModel
import com.app.xiaobai.music.music.view.fragment.CoverFragment
import com.app.xiaobai.music.music.view.fragment.LyricFragment
import com.app.xiaobai.music.service.LockService
import com.app.xiaobai.music.service.MusicService
import com.app.xiaobai.music.service.PlayService
import com.app.xiaobai.music.sql.bean.Down
import com.app.xiaobai.music.sql.bean.Playlist
import com.app.xiaobai.music.sql.dao.mCollectDao
import com.app.xiaobai.music.sql.dao.mDownDao
import com.app.xiaobai.music.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import com.lzx.starrysky.StarrySky
import com.lzx.starrysky.common.PlaybackStage
import com.lzy.okgo.OkGo
import com.lzy.okserver.OkDownload
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
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
        var position: Int = 0
        var song_id: Long = 0
        lateinit var load: String
        var id: Int = 0
        lateinit var observer: Observer<Boolean>
        lateinit var observers: Observer<Boolean>
        lateinit var observerui: Observer<Long>

        lateinit var observerset: Observer<Int>
        lateinit var observerseek: Observer<Long>
        lateinit var observerseeks: Observer<Long>
        lateinit var observerly: Observer<Long>
        lateinit var observerplay: Observer<Int>
        lateinit var adapter: PlaySongAdapter
        lateinit var t1: String
        lateinit var t2: String
        lateinit var m: String
        lateinit var uri: String
        var max: Long = 0
        lateinit var playingMusicList: MutableList<Music>
    }

    private var style: Int = 0
    private var adaptert: PlayListAdapter? = null
    private var count: Int = 2

    private var search: Boolean = false
    private lateinit var sp: SharedPreferences
    private var min: Long = 0
    private lateinit var playingMusic: Music
    private var coverFragment = CoverFragment()
    private var lyricFragment = LyricFragment()
    private val fragments = mutableListOf<Fragment>()
    private lateinit var context: Context
    private lateinit var downs: MutableList<Down>

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

        initView()
        initData()
        m = "http://p1.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg"
        MusicApp.setBool(true)



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
                        musicplay(6, position, id)
                        Toast.makeText(
                            context,
                            getText(R.string.sui),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        icon1.setImageResource(R.drawable.xun)
                        musicplay(6, position, id)
                        Toast.makeText(
                            context,
                            getText(R.string.lie),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    count = 0
                    icon1.setImageResource(R.drawable.dan)
                    musicplay(6, position, id)
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
                if (MusicApp.network() == -1) {
                    Toast.makeText(
                        MusicApp.getAppContext(),
                        getText(R.string.error_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (MusicApp.userlogin()) {
                        if (style != 4) {
                            if (downs.size > 0) {
                                MaterialDialog.Builder(context)
                                    .title(getText(R.string.song_delsong))
                                    .content(getText(R.string.song_delsongs))
                                    .positiveColorRes(R.color.colorAccentDarkTheme)
                                    .negativeColorRes(R.color.red)
                                    .positiveText(getText(R.string.carry))
                                    .negativeText(getText(R.string.cancel))
                                    .onPositive { _: MaterialDialog?, _: DialogAction? ->
                                        mDownDao.delete(downs[0].id)
                                        icon2.setImageResource(R.drawable.xiazai)
                                        Toast.makeText(
                                            context,
                                            getText(R.string.song_delsongsucc),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .show()

                            } else {
                                if (uri != "") {
                                    if(Constants.Downnum()){
                                        val request = OkGo.get<File>(uri)
                                        OkDownload.request(uri, request) //
                                            .priority(0)
                                            .folder(context.getExternalFilesDir("")!!.absolutePath+"/download")
                                            .fileName("music$song_id") //
                                            .save() //
                                            .register(
                                                LogDownloadListener(
                                                    playingMusic,
                                                    context,
                                                    0,
                                                    downs
                                                )
                                            ) //
                                            .start()
                                        icon2.setImageResource(R.drawable.xiazais)
                                    }else{
                                        Toast.makeText(
                                            context,
                                            getText(R.string.download_num),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                } else {
                                    Toast.makeText(
                                        context,
                                        getText(R.string.download_error),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        } else {
                            MaterialDialog.Builder(context)
                                .title(getText(R.string.song_delsong))
                                .content(getText(R.string.song_delsongs))
                                .positiveColorRes(R.color.colorAccentDarkTheme)
                                .negativeColorRes(R.color.red)
                                .positiveText(getText(R.string.carry))
                                .negativeText(getText(R.string.cancel))
                                .onPositive { _: MaterialDialog?, _: DialogAction? ->
                                    val downs = mDownDao.querys(song_id)
                                    if (downs.size > 0) {
                                        mDownDao.delete(downs[0].id)
                                        icon2.setImageResource(R.drawable.xiazai)
                                        Toast.makeText(
                                            context,
                                            getText(R.string.song_delsongsucc),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            getText(R.string.song_download_error),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                .show()
                        }


                    } else {
                        MaterialDialog.Builder(context)
                            .title(getText(R.string.go))
                            .content(getText(R.string.ungoset))
                            .positiveText(getText(R.string.carry))
                            .negativeText(getText(R.string.cancel))
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

            }

        RxView.clicks(icon3)
            .throttleFirst(0, TimeUnit.SECONDS)
            .subscribe {
                if (MusicApp.network() == -1) {
                    Toast.makeText(
                        MusicApp.getAppContext(),
                        getText(R.string.error_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
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
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    } else {
                        MaterialDialog.Builder(context)
                            .title(getText(R.string.go))
                            .content(getText(R.string.ungoset))
                            .positiveText(getText(R.string.carry))
                            .negativeText(getText(R.string.cancel))
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
                    .title(getText(R.string.song_list))
                    .content(getText(R.string.song_dellist))
                    .positiveText(getText(R.string.carry))
                    .negativeText(getText(R.string.cancel))
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

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                println("点击seek"+ position)
                musicplay(5, position, id)
            }
        })
    }


    fun initData() {
        val bundle = intent.extras
        val album_id = bundle?.get("album_id") as Long
        val pos = bundle.get("pos") as Int
        val list = bundle.get("list") as String
        val styles = bundle.get("type") as Int


        when (styles) {
            0 -> {
                musicplay(7, 0, pos)
            }
            1 -> {
                style = 1
                removedata(true, album_id, pos, list)
            }
            2 -> {
                style = 1
                removedata(false, album_id, pos, list)
            }
            3 -> {
                style = 3
                removedata(false, album_id, pos, list)
            }
            4 -> {
                style = 4
                removedata(true, album_id, pos, list)
            }
        }

    }

    fun removedata(t: Boolean, album_id: Long, pos: Int, list: String) {
        if (t) {
            val obj: JsonArray = Gson().fromJson(list, JsonArray::class.java)
            val song: MutableList<Music> = Gson().fromJson(
                obj,
                object : TypeToken<MutableList<Music>>() {}.type
            )
            song.removeAt(0)
            if (song.isNotEmpty()) {
                if (song[pos].song_id == song_id && pos == id) {
                    musicplay(7, 0, pos)
                } else {
                    if(MusicApp.getAblumid()==album_id){
                        MusicApp.setMusic(song)
                        MusicApp.setPosition(pos)
                        playPauseIv.setLoading(true)
                        musicplay(0, 0, pos)
                    }else{
                        MusicApp.setAblumid(album_id)
                        MusicApp.setMusic(song)
                        playingMusicList = song
                        MusicApp.setPosition(pos)
                        playPauseIv.setLoading(true)
                        musicplay(8, 0, pos)
                    }

                }

            }
        } else {
            val obj: JsonArray = Gson().fromJson(list, JsonArray::class.java)
            val song: MutableList<Music> = Gson().fromJson(
                obj,
                object : TypeToken<MutableList<Music>>() {}.type
            )
            if (song.isNotEmpty()) {
                if (song[pos].song_id == song_id && pos == id) {
                    musicplay(7, 0, pos)
                } else {
                    if(MusicApp.getAblumid()==album_id){
                        MusicApp.setMusic(song)
                        MusicApp.setPosition(pos)
                        playPauseIv.setLoading(true)
                        musicplay(0, 0, pos)
                    }else{
                        MusicApp.setAblumid(album_id)
                        MusicApp.setMusic(song)
                        playingMusicList = song
                        MusicApp.setPosition(pos)
                        playPauseIv.setLoading(true)
                        musicplay(8, 0, pos)
                    }

                }
            }
        }

    }


    fun del() {
        val intentservice = Intent(this, MusicService::class.java)
        stopService(intentservice)


        val lockservice = Intent(this, LockService::class.java)
        stopService(lockservice)

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
                    progressSb.progress = 0
                    progressTv.text = "00:00"
                    id = MusicApp.getPosition()
                    playingMusic = playingMusicList[id]
                    song_id = playingMusic.song_id
                    try {
                        downs = mDownDao.querys(song_id)
                        if (downs.size > 0) {
                            icon2.setImageResource(R.drawable.xiazais)
                        } else {
                            icon2.setImageResource(R.drawable.xiazai)
                        }
                    }catch (e:java.lang.Exception){
                        icon2.setImageResource(R.drawable.xiazai)
                    }


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
                    search = playingMusic.uri == ""
                    subTitleTv.text = srtist_name
                    Ablemname.text = playingMusic.album_name
                    coverFragment.setImagePath(playingMusic.pic_url)

                    t1 = playingMusic.name
                    t2 = srtist_name
                    m = playingMusic.pic_url
                    lyricFragment.lrcView(playingMusic.song_id)
                    Notifications.init(1)
                    if (MusicApp.getLock()) {
                        LockActivity.data()
                    }
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        //开始播放
        observerui = object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {}

            @SuppressLint("SetTextI18n")
            override fun onNext(duration: Long) {
                coverFragment.setImagePath(playingMusic.pic_url)


                max = duration
                progressSb.max = duration.toInt()
                val f = duration / 60000
                val m = duration % 60000
                durationTv.text =
                    unitFormat(f.toInt()) + ":" + unitFormat(m.toInt())
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
                playPauseIv.setLoading(false)
                playPauseIv.play()
                MusicApp.setPlay(true)
                coverFragment.startRotateAnimation(true)
            }

        }


        //播放、上一首、下一首
        observerset = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: Int) {
                try {
                    when (data) {
                        0 -> {
                            musicplay(3, 0, id)
                        }
                        1 -> {

                            musicplay(1, 0, id)
                        }
                        2 -> {
                            musicplay(2, 0, id)
                        }
                        3 -> {
                            if (load=="pause") {
                                musicplay(4, 0, id)
                            } else {
                                playPauseIv.setLoading(true)
                                musicplay(0, 0, id)
                            }

                        }
                    }
                } catch (e: Exception) {
                }


            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }


        //进度更新接口
        observerseek = object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {}

            @SuppressLint("SetTextI18n")
            override fun onNext(bools: Long) {
                try {
                    min = bools
                    val fs = min / 60000
                    val ms = min % 60000
                    progressTv.text = unitFormat(fs.toInt()) + ":" + unitFormat(ms.toInt())
                    progressSb.progress = min.toInt()
                    lrcView.updateTime(min)

                } catch (e: java.lang.Exception) {
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        observerseeks = object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {}

            @SuppressLint("SetTextI18n")
            override fun onNext(bool: Long) {
                progressSb.secondaryProgress = bool.toInt()

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        observerly = object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {}

            @SuppressLint("SetTextI18n")
            override fun onNext(bool: Long) {
                musicplay(5, (bool).toInt(), id)

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
                        progressSb.progress = 0
                        progressTv.text = "00:00"
                        playPauseIv.setLoading(true)
                    }
                    1 -> {
                        playPauseIv.setLoading(false)
                    }
                    2 -> {
                        try {
                            MusicApp.setPlay(false)
                            if (playPauseIv.isPlaying) {
                                playPauseIv.pause()
                                coverFragment.stopRotateAnimation()
                            }
                        } catch (e: java.lang.Exception) {
                        }

                    }
                    3 -> {
                        try {
                            MusicApp.setPlay(false)
                            if (playPauseIv.isPlaying) {
                                playPauseIv.pause()
                                coverFragment.stopRotateAnimation()
                            }
                        } catch (e: java.lang.Exception) {
                        }
                    }
                    4 -> {
                        try {
                            playPauseIv.setLoading(false)
                            MusicApp.setPlay(true)
                            if (!playPauseIv.isPlaying) {
                                playPauseIv.play()
                                coverFragment.resumeRotateAnimation()
                            }
                        } catch (e: java.lang.Exception) {
                        }
                    }
                    5 -> {
                        try {
                            progressSb.progress = 0
                            progressTv.text = "00:00"
                            MusicApp.setPlay(false)
                            playPauseIv.setLoading(false)
                            if (playPauseIv.isPlaying) {
                                playPauseIv.pause()
                                coverFragment.stopRotateAnimation()
                            }
                            Toast.makeText(
                                context,
                                getText(R.string.error_playing_trackt),
                                Toast.LENGTH_SHORT
                            ).show()
                            musicplay(2, 0, id)
                        } catch (e: java.lang.Exception) {
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
                    playPauseIv.setLoading(true)
                    musicplay(0, 0, position)
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
                MaterialDialog.Builder(context)
                    .title(getText(R.string.song_addsong))
                    .content(getText(R.string.song_addsonglist))
                    .positiveText(getText(R.string.carry))
                    .negativeText(getText(R.string.cancel))
                    .positiveColorRes(R.color.colorAccentDarkTheme)
                    .negativeColorRes(R.color.red)
                    .onPositive { _: MaterialDialog?, _: DialogAction? ->
                        val idmap = mutableListOf<Music>()
                        playingMusic.let { idmap.add(it) }
                        val playlist: Playlist =
                            mPlaylistDao.query(song[position].play_list_id)[0]
                        val playsong = mCollectDao.query(song[position].play_list_id)
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
                                    val num =
                                        (playlist.song_num.toInt() + songs.size).toString()
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
                                        Toast.LENGTH_SHORT
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
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    .show()

            }
        })
    }


    fun musicplay(type: Int, seek: Int, id: Int) {


        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("type", type)
        intent.putExtra("style", style)
        intent.putExtra("seek", seek)
        intent.putExtra("count", count)
        intent.putExtra("id", id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android8.0以上通过startForegroundService启动service
            startForegroundService(intent)
        } else {
            startService(intent)
        }
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

