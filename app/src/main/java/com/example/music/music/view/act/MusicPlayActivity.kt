package com.example.music.music.view.act

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.music.R
import com.example.music.adapter.ViewPagerAdapter
import com.example.music.bean.Music
import com.example.music.music.view.fragment.CoverFragment
import com.example.music.music.view.fragment.LyricFragment
import com.example.music.utils.BitmapUtils
import com.ywl5320.libenum.MuteEnum
import com.ywl5320.libmusic.WlMusic
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.music_play.*
import java.util.concurrent.TimeUnit

class MusicPlayActivity : AppCompatActivity() {

    companion object {
        var position: Int = 0
        lateinit var wlMusic: WlMusic
        lateinit var observer: Observer<Music>
        lateinit var observernext: Observer<String>
    }


    private lateinit var mDisposable: Disposable
    private var bool: Boolean = false
    private var min: Long = 0
    private var max: Long = 0
    private var bitmap: Bitmap? = null
    private var playingMusic: Music? = null
    private var coverFragment= CoverFragment()
    private var lyricFragment= LyricFragment()
    private val fragments = mutableListOf<Fragment>()
    private lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_play)
        context = this
        initView()
        initData()
    }

    fun initView(){
        detailView.animation = moveToViewLocation()
        fragments.add(coverFragment)
        fragments.add(lyricFragment)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        coverFragment.initAlbumPic()
        playPauseIv.setLoading(true)
        playPauseIv.setOnClickListener {
            if (bool) {
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

    }

     fun initData() {
        val bundle = this.intent.extras
        val music = bundle?.get("music") as Music
        playingMusic = music
        //更新标题
        titleIv.text = music.title
        subTitleTv.text = music.artist
        Ablemname.text = music.album
        //更新收藏状态
        /* music?.isLove?.let {
             collectIv.setImageResource(if (it) R.drawable.item_favorite_love else R.drawable.item_favorite)
         }*/
         object : Thread() {
             override fun run() {
                 bitmap = BitmapUtils.netUrlPicToBmp(music.coverBig.toString())
                 coverFragment.setImageBitmap(bitmap)
                 play(music.uri)
             }
         }.start()
    }

    override fun onStart() {
        super.onStart()
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        observer = object : Observer<Music> {
            override fun onSubscribe(d: Disposable) {}

            @SuppressLint("SetTextI18n")
            override fun onNext(data: Music) {
                //play(data.uri)
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        observernext = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(uri: String) {
                wlMusic.stop()
                wlMusic.playNext(uri)
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

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
            Observable.just(wlMusic.duration.toLong())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(aLong: Long) {
                        bool = true
                        max = aLong
                        progressSb.max = wlMusic.duration
                        val f = aLong / 60
                        val m = aLong % 60
                        progressTv.text = "00:00"
                        durationTv.text = unitFormat(f.toInt()) + ":" + unitFormat(m.toInt())
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
                wlMusic.seek(position, true, true) //表示seek已经完成，然后才回调时间，避免自己控制时间逻辑和时间显示不稳定问题。
                if (!mDisposable.isDisposed) {
                    mDisposable.dispose()
                    time(position.toLong(), max - position)
                }
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
            }
            .doOnComplete {
                playPauseIv.pause()
                coverFragment.onStop()
            }
            .subscribe()
    }

    fun unitFormat(time: Int): String {
        return if (time in 0..9)
            "0$time"
        else
            "" + time
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

}
