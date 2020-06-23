package com.example.music.xiaobai.music.view.act

import android.annotation.SuppressLint
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
import com.bumptech.glide.Glide
import com.example.music.xiaobai.MusicApp
import com.example.music.xiaobai.R
import com.example.music.xiaobai.adapter.PlayListAdapter
import com.example.music.xiaobai.adapter.PlaySongAdapter
import com.example.music.xiaobai.adapter.ViewPagerAdapter
import com.example.music.xiaobai.bean.Music
import com.example.music.xiaobai.config.LogDownloadListener
import com.example.music.xiaobai.music.model.MusicPlayModel
import com.example.music.xiaobai.music.view.fragment.CoverFragment
import com.example.music.xiaobai.music.view.fragment.LyricFragment
import com.example.music.xiaobai.sql.bean.Playlist
import com.example.music.xiaobai.sql.dao.mDownDao
import com.example.music.xiaobai.sql.dao.mPlaylistDao
import com.example.music.xiaobai.utils.BitmapUtils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import com.lzy.okgo.OkGo
import com.lzy.okserver.OkDownload
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.ywl5320.libenum.MuteEnum
import com.ywl5320.libmusic.WlMusic
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.album_index.*
import kotlinx.android.synthetic.main.frag_player_lrcview.*
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.music_play.*
import kotlinx.android.synthetic.main.music_play.in_indel
import kotlinx.android.synthetic.main.play_list.*
import java.io.File
import java.util.concurrent.TimeUnit

class MusicPlayActivity : AppCompatActivity() {

    companion object {
        var position: Int = 0
        var song_id: Long = 0
        lateinit var wlMusic: WlMusic
        lateinit var observer: Observer<String>
        lateinit var observers: Observer<Long>
        lateinit var observerplay: Observer<MutableList<Music>>
        lateinit var observerset: Observer<Int>
        var bool: Boolean = false
        var play: Boolean = false
        var id: Int = 0
        lateinit var adapter: PlaySongAdapter
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
    private var playingMusicList: MutableList<Music>? = null
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
    }


    @SuppressLint("CheckResult")
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
            }

        RxView.clicks(icon1)
            .throttleFirst(0, TimeUnit.SECONDS)
            .subscribe {
                if (type < 2) {
                    type++
                    if (type == 1) {
                        Glide.with(context).load(R.drawable.sui).into(icon1)
                    } else {
                        Glide.with(context).load(R.drawable.xun).into(icon1)
                    }

                } else {
                    type = 0
                    Glide.with(context).load(R.drawable.dan).into(icon1)
                }

            }

        RxView.clicks(icon2)
            .throttleFirst(0, TimeUnit.SECONDS)
            .subscribe {

                MaterialDialog.Builder(context)
                    .title("下载音乐")
                    .content("是否下载音乐")
                    .positiveText("确认")
                    .negativeText("取消")
                    .onPositive { _: MaterialDialog?, _: DialogAction? ->

                        val downs = mDownDao.querys(playingMusic!!.song_id)
                        if(downs.size>0){
                            for(its in downs){
                                if(its.type==0){
                                    val request = OkGo.get<File>(playingMusic!!.uri)
                                    OkDownload.request(playingMusic!!.uri, request) //
                                        .priority(0)
                                        .fileName("music" + playingMusic!!.song_id + ".mp3") //
                                        .save() //
                                        .register(LogDownloadListener(playingMusic, context, 0,downs,0)) //
                                        .start()
                                }else{
                                    Toast.makeText(
                                        context,
                                        getText(R.string.download_carry),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }else{
                            val request = OkGo.get<File>(playingMusic!!.uri)
                            OkDownload.request(playingMusic!!.uri, request) //
                                .priority(0)
                                .fileName("music" + playingMusic!!.song_id + ".mp3") //
                                .save() //
                                .register(LogDownloadListener(playingMusic, context, 1,downs,1)) //
                                .start()
                        }


                    }
                    .show()
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
        } else if(types==2){
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
            } else if(types==2) {
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


        }
    }

    override fun onResume() {
        super.onResume()

        observer = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(num: String) {

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
                when (data) {
                    0 -> {
                        if (playPauseIv.isPlaying) {
                            playPauseIv.pause()
                            wlMusic.stop()
                            mDisposable.dispose()
                            play = false
                            coverFragment.stopRotateAnimation()
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
                            wlMusic.resume()
                            play = true
                            time(min, max - min)
                            coverFragment.resumeRotateAnimation()
                        }

                    }
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
                println(position)
                id = position
                adaptert!!.notifyItemChanged(id)
                starts(playingMusicList!![position])

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
            override fun onItemClick(view: View, position: Int) {
                pos = position
                MaterialDialog.Builder(context)
                    .title("添加音乐")
                    .content("是否将音乐加入此歌单")
                    .positiveText("确认")
                    .negativeText("取消")
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
            lyricFragment.lrcView(music.song_id)
            object : Thread() {
                override fun run() {
                    bitmap = BitmapUtils.netUrlPicToBmp(music.pic_url)
                    coverFragment.setImageBitmap(bitmap)
                    play(music.uri)
                }
            }.start()
        } catch (e: Exception) {
        }

    }

    private fun starts(music: Music) {
        try {
            if (playPauseIv.isPlaying) {
                playPauseIv.pause()
                play = false
                coverFragment.stopRotateAnimation()
            }
            wlMusic.stop()
            mDisposable.dispose()

            playPauseIv.setLoading(true)
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
            lyricFragment.lrcView(music.song_id)
            object : Thread() {
                override fun run() {
                    bitmap = BitmapUtils.netUrlPicToBmp(music.pic_url)
                    coverFragment.setImageBitmap(bitmap)
                    wlMusic.playNext(music.uri)

                }
            }.start()
        } catch (e: Exception) {
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        if (wlMusic.isPlaying) {
            wlMusic.stop()
        }
    }


    @SuppressLint("SetTextI18n")
    fun play(uri: String?) {
        wlMusic = WlMusic.getInstance()
        wlMusic.source = uri //设置音频源
        wlMusic.setCallBackPcmData(false) //是否返回音频PCM数据
        wlMusic.setShowPCMDB(false) //是否返回音频分贝大小
        wlMusic.isPlayCircle = false //设置不间断循环播放音频
        wlMusic.volume = 100 //设置音量 65%
        wlMusic.playSpeed = 1.0f //设置播放速度 (1.0正常) 范围：0.25---4.0f
        wlMusic.playPitch = 1.0f //设置播放速度 (1.0正常) 范围：0.25---4.0f
        wlMusic.mute = MuteEnum.MUTE_CENTER //设置立体声（左声道、右声道和立体声）
        wlMusic.setConvertSampleRate(null) //设定恒定采样率（null为取消）
        wlMusic.prePared()
        wlMusic.setOnPreparedListener {
            if (wlMusic.duration > 0) {
                Observable.just((wlMusic.duration).toLong())
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
                            wlMusic.start() //准备完成开始播放
                            play = true
                            coverFragment.startRotateAnimation(wlMusic.isPlaying)
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


        progressSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                position = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                wlMusic.seek(position, false, false) // 表示在seeking中，此时不回调时间
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                wlMusic.seek(position, true, true) //表示seek已经完成，然后才回调时间，避免自己控制时间逻辑和时间显示不稳定问题。
                Observable.just(position.toLong()).subscribe(observers)
            }
        })

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
    }

}
