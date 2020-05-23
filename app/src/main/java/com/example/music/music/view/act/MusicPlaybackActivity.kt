package com.example.music.music.view.act

import android.content.Context
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import com.example.music.R
import com.example.music.bean.HomeList
import com.example.music.bean.Music
import com.example.music.music.contract.MusicPlaybackContract
import com.example.music.music.presenter.MusicPlaybackPresenter
import com.example.music.music.view.fragment.CoverFragment
import com.example.music.music.view.fragment.LyricFragment
import com.ywl5320.libenum.MuteEnum
import com.ywl5320.libmusic.WlMusic
import com.ywl5320.util.RawAssetsUtil
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.music_playback.*
import mvp.ljb.kt.act.BaseMvpActivity

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/15
 * @Description input description
 **/

class MusicPlaybackActivity : BaseMvpActivity<MusicPlaybackContract.IPresenter>(),
    MusicPlaybackContract.IView {

    companion object {
        var position:Int = 0
        lateinit var wlMusic: WlMusic
        lateinit var observer: Observer<Music>
        lateinit var observernext: Observer<String>
    }


    private var playingMusic: Music? = null
    private var coverFragment: CoverFragment? = CoverFragment()
    private var lyricFragment: LyricFragment? = LyricFragment()

    private val fragments = mutableListOf<Fragment>()
    private lateinit var context: Context
    override fun registerPresenter() = MusicPlaybackPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.music_playback
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this


    }

    override fun initData() {
        super.initData()
        coverFragment?.initAlbumPic()
        val bundle = this.intent.extras
        val music = bundle?.get("musiclist") as HomeList

    }

    override fun initView() {
        super.initView()
        detailView.animation = moveToViewLocation()
        play( "http://music.163.com/song/media/outer/url?id=1327199767.mp3")
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
            override fun onNext(data: Music) {
                play(data.uri)
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


    fun play(url: String?) {
        wlMusic = WlMusic.getInstance()
        wlMusic.source = url //设置音频源
        wlMusic.setCallBackPcmData(false) //是否返回音频PCM数据
        wlMusic.setShowPCMDB(false) //是否返回音频分贝大小
        wlMusic.isPlayCircle = true //设置不间断循环播放音频
        wlMusic.volume = 100 //设置音量 65%
        wlMusic.playSpeed = 1.0f //设置播放速度 (1.0正常) 范围：0.25---4.0f
        wlMusic.playPitch = 1.0f //设置播放速度 (1.0正常) 范围：0.25---4.0f
        wlMusic.mute = MuteEnum.MUTE_CENTER //设置立体声（左声道、右声道和立体声）
        wlMusic.setConvertSampleRate(null) //设定恒定采样率（null为取消）
        wlMusic.prePared()
        wlMusic.setOnPreparedListener {
            println("开始播放")
            wlMusic.start() //准备完成开始播放
        }

        progressSb.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                position = wlMusic.duration * progress / 100
                println("seekbar$position")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                wlMusic.seek(position, false, false) // 表示在seeking中，此时不回调时间
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                wlMusic.seek(position, true, true) //表示seek已经完成，然后才回调时间，避免自己控制时间逻辑和时间显示不稳定问题。
            }
        })
    }

    /***
     * 显示当前正在播放
     */
    fun showNowPlaying(music: Music?) {
        if (music == null) finish()

        playingMusic = music
        //更新标题
        titleIv.text = music?.title
        subTitleTv.text = music?.artist
        //更新类型
        // music?.let { coverFragment?.updateMusicType(it) }
        //更新收藏状态
       /* music?.isLove?.let {
            collectIv.setImageResource(if (it) R.drawable.item_favorite_love else R.drawable.item_favorite)
        }*/
        //开始旋转动画
        coverFragment?.startRotateAnimation(wlMusic.isPlaying)
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
