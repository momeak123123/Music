package com.example.xiaobai.music.music.view.fragment

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import com.bumptech.glide.Glide
import com.example.xiaobai.music.R
import com.example.xiaobai.music.music.contract.CoverContract
import com.example.xiaobai.music.music.presenter.CoverPresenter
import com.example.xiaobai.music.music.view.act.MusicPlayActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.frag_player_coverview.*
import mvp.ljb.kt.fragment.BaseMvpFragment


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class CoverFragment : BaseMvpFragment<CoverContract.IPresenter>(), CoverContract.IView {

    override fun registerPresenter() = CoverPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.frag_player_coverview
    }

    private lateinit var mAnimator: ObjectAnimator

    //旋转属性动画
    private var coverAnimator: ObjectAnimator? = null

    override fun initView() {
        super.initView()
        initAnimator()//进入页面加载动画
    }

    /**
     * 设置Bitmap
     */
    fun setImageBitmap(context: Context, bm: String) {

        //iv_cover.setImageURI(Uri.parse(bm))
        Observable.just(bm)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(cover: String) {
                    Glide.with(context).load(cover).placeholder(R.color.white_8p)
                        .into(iv_covers)
                }
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })


    }


    fun initAnimator() {
        mAnimator = ObjectAnimator.ofFloat(iv_covers, "rotation", 0.0f, 360.0f)
        mAnimator.duration = 9600//设定转一圈的时间
        mAnimator.repeatCount = Animation.INFINITE//设定无限循环
        mAnimator.repeatMode = ObjectAnimator.RESTART// 循环模式
        mAnimator.interpolator = object : LinearInterpolator() {}
        mAnimator.start()//动画开始
        mAnimator.pause()
    }


    /**
     * 切换歌曲，开始旋转动画
     */
    fun startRotateAnimation(isPlaying: Boolean = false) {
        if (isPlaying) {
            coverAnimator?.cancel()
            coverAnimator?.start()
            mAnimator.start()
        }
    }

    /**
     * 停止旋转
     */
    fun stopRotateAnimation() {
        coverAnimator?.pause()
        mAnimator.pause()
    }

    /**
     * 继续旋转
     */
    fun resumeRotateAnimation() {
        coverAnimator?.isStarted?.let {
            if (it) coverAnimator?.resume() else coverAnimator?.start()
        }
        mAnimator.resume()
    }

    override fun onResume() {
        super.onResume()
        if (coverAnimator != null && coverAnimator?.isPaused!! && MusicPlayActivity.wlMedia.isPlaying) {
            coverAnimator?.resume()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coverAnimator?.cancel()
        coverAnimator = null
    }

    override fun onStop() {
        super.onStop()
        coverAnimator?.pause()
    }
}

