package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import com.example.music.MainActivity
import com.example.music.R
import com.example.music.music.contract.StartPageContract
import com.example.music.music.presenter.StartPagePresenter
import com.example.music.utils.NetWorkUtils
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.music_play.*
import kotlinx.android.synthetic.main.start_page.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class StartPageActivity : BaseMvpActivity<StartPageContract.IPresenter>() , StartPageContract.IView {


    private lateinit var mDisposable: Disposable
    private lateinit var context: Context
    private lateinit var countDownTimer: CountDownTimer

    override fun registerPresenter() = StartPagePresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.start_page
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context=this
        MainActivity.bool = false
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        mDisposable = Flowable.intervalRange(0, 4, 0, 1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { t ->
                time.text=(3-t).toString()
            }
            .doOnComplete {
               finish()
            }
            .subscribe()

        RxView.clicks(view)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                mDisposable.dispose()
               finish()
            }
    }

    override fun initData() {
        super.initData()
        context.let { getPresenter().homedata(it) }
        //Toast.makeText(context, NetWorkUtils.getNetworkTypeName(context), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
