package com.app.xiaobai.music.music.view.fragment

import android.animation.ObjectAnimator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import com.bumptech.glide.Glide
import com.app.xiaobai.music.IndexActivity
import com.app.xiaobai.music.R
import com.app.xiaobai.music.music.contract.CoverContract
import com.app.xiaobai.music.music.presenter.CoverPresenter
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.frag_player_coverviews.*
import mvp.ljb.kt.fragment.BaseMvpFragment


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class CoverFragment : BaseMvpFragment<CoverContract.IPresenter>(), CoverContract.IView {

    override fun registerPresenter() = CoverPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.frag_player_coverviews
    }

    private lateinit var mAnimator: ObjectAnimator

    override fun initView() {
        super.initView()
        initAnimator()//进入页面加载动画
    }

    /**
     * 设置Bitmap
     */
    fun setImagePath(ima:String ) {
        Observable.just(0)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(cover: Int) {
                    context?.let { Glide.with(it).load(ima).placeholder(R.drawable.undetback).into(iv_cover) }
                }
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })


    }

    fun initAnimator() {
        mAnimator = ObjectAnimator.ofFloat(iv_cover, "rotation", 0.0f, 360.0f)
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
        try{
            if (isPlaying) {
                mAnimator.start()
                Observable.just(0).subscribe(IndexActivity.observers)
            }

        }catch (e:Exception){}

    }

    /**
     * 停止旋转
     */
    fun stopRotateAnimation() {
        try{
            mAnimator.pause()
            Observable.just(1).subscribe(IndexActivity.observers)
        }catch (e:Exception){}

    }

    /**
     * 继续旋转
     */
    fun resumeRotateAnimation() {
        try{
            mAnimator.resume()
            Observable.just(2).subscribe(IndexActivity.observers)
        }catch (e:Exception){}

    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onStop() {
        super.onStop()
    }
}

