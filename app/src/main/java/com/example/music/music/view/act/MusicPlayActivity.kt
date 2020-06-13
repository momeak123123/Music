package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.adapter.PlayListAdapter
import com.example.music.adapter.PlaySongAdapter
import com.example.music.adapter.ViewPagerAdapter
import com.example.music.bean.Music
import com.example.music.config.ItemClickListener
import com.example.music.music.view.fragment.CoverFragment
import com.example.music.music.view.fragment.LyricFragment
import com.example.music.sql.bean.Playlist
import com.example.music.sql.dao.mPlaylistDao
import com.example.music.utils.BitmapUtils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.ywl5320.libenum.MuteEnum
import com.ywl5320.libmusic.WlMusic
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.frag_player_lrcview.*
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.music_play.*
import kotlinx.android.synthetic.main.play_list.*
import java.util.concurrent.TimeUnit

class MusicPlayActivity : AppCompatActivity() {

    companion object {
        var position: Int = 0
        var id: Int = 0
        lateinit var wlMusic: WlMusic
        lateinit var observers: Observer<Long>
    }

    private var type: Int = 0
    var song_id: Long = 0
    lateinit var mDisposable: Disposable
    var max: Long = 0
    private var bool: Boolean = false
    private var min: Long = 0
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
        initView()
        initData()
    }


    @SuppressLint("CheckResult")
    private fun initView() {
        playPauseIv.setOnClickListener {
            if(bool){
                if (playPauseIv.isPlaying) {
                    playPauseIv.pause()
                    wlMusic.pause()
                    mDisposable.dispose()
                    coverFragment.stopRotateAnimation()
                } else {
                    playPauseIv.play()
                    wlMusic.resume()
                    time(min, max - min)
                    coverFragment.resumeRotateAnimation()
                }
            }

        }

        RxView.clicks(top_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                moveTaskToBack(true)
            }

        RxView.clicks(icon1)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if(type<2){
                    type++
                    if(type==1){
                        Glide.with(context).load(R.drawable.dan).placeholder(R.color.main_black_grey).into(icon1)
                    }else{
                        Glide.with(context).load(R.drawable.sui).placeholder(R.color.main_black_grey).into(icon1)
                    }

                }else{
                    type=0
                    Glide.with(context).load(R.drawable.xun).placeholder(R.color.main_black_grey)
                        .into(icon1)
                }

            }

        RxView.clicks(icon2)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {

            }

        RxView.clicks(icon3)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                in_indel.visibility = View.VISIBLE
                Glide.with(context).load("").into(del)
                in_title.text = R.string.song_but.toString()
                val list: MutableList<Playlist> =  mPlaylistDao.queryAll()
                initSongList(list)
            }

        RxView.clicks(icon4)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                in_indel.visibility = View.VISIBLE
                Glide.with(this).load(R.drawable.list_del).placeholder(R.color.main_black_grey)
                    .into(del)
                in_title.text = ""
                initPlayList()
            }

        RxView.clicks(list_dow)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                in_indel.visibility = View.GONE
            }


    }

    fun initData() {
        val bundle = intent.extras
        val pos = bundle?.get("pos") as Int
        val list = bundle.get("list") as String

        val obj: JsonArray = Gson().fromJson(list, JsonArray::class.java)
        val song: MutableList<Music> = Gson().fromJson(
            obj,
            object : TypeToken<MutableList<Music>>() {}.type
        )

        if (song.isNotEmpty()) {
            id = pos
            song_id = song[pos].song_id
            playingMusicList = song
            playingMusic = song[pos]
            start(playingMusic!!)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            val bundle = intent.extras
            val pos = bundle?.get("pos") as Int
            val list = bundle.get("list") as String

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
                    playingMusicList = song
                    playingMusic = song[pos]
                    starts(playingMusic!!)


                }

            }
        }
    }

    override fun onResume() {
        super.onResume()

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
    }

    /**
     * 初始化歌曲
     */
    private fun initPlayList() {
        in_list.layoutManager = LinearLayoutManager(context)
        in_list.itemAnimator = DefaultItemAnimator()
        val adapter = playingMusicList?.let { PlayListAdapter(it, context) }
        in_list.adapter = adapter
        in_list.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        starts(playingMusicList!![position])
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }

    /**
     * 初始化歌曲
     */
    private fun initSongList(song: MutableList<Playlist>) {
        in_list.layoutManager = LinearLayoutManager(context)
        in_list.itemAnimator = DefaultItemAnimator()
        val adapter = PlaySongAdapter(song, context)
        in_list.adapter = adapter
        in_list.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        MaterialDialog.Builder(context)
                            .title("添加音乐")
                            .content("是否将音乐加入此歌单")
                            .positiveText("确认")
                            .negativeText("取消")
                            .onPositive { _: MaterialDialog?, _: DialogAction? ->

                            }
                            .show()
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }

    private fun start(music: Music) {
        playPauseIv.setLoading(true)
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
                play(music.uri)
            }
        }.start()
    }

    private fun starts(music: Music) {
        wlMusic.stop()
        playPauseIv.pause()
        playPauseIv.setLoading(true)
        mDisposable.dispose()
        coverFragment.stopRotateAnimation()
        Observable.just(0L).subscribe(observers)
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
                        coverFragment.startRotateAnimation(wlMusic.isPlaying)
                        time(0, max)
                    }
                })

        }


        progressSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                position = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                wlMusic.seek(position, false, false) // 表示在seeking中，此时不回调时间
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                wlMusic.seek(position , true, true) //表示seek已经完成，然后才回调时间，避免自己控制时间逻辑和时间显示不稳定问题。
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
                lrcView.updateTime(t*1000)
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
            1 -> {
                //单曲循环
                starts(playingMusic!!)
            }
            2 -> {
                //随机播放
                val randoms = (0 until playingMusicList!!.size).random()
                id = randoms
                starts(playingMusicList!![randoms])
            }
            else -> {
                //列表循环
                val ids = id + 1
                if (playingMusicList!!.size == ids) {
                    id = 0
                    starts(playingMusicList!![0])
                } else {
                    id = ids
                    starts(playingMusicList!![ids])
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
