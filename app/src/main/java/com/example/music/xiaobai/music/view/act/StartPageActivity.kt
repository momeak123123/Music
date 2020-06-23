package com.example.music.xiaobai.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.example.music.xiaobai.MainActivity
import com.example.music.xiaobai.R
import com.example.music.xiaobai.music.contract.StartPageContract
import com.example.music.xiaobai.music.presenter.StartPagePresenter
import com.jakewharton.rxbinding2.view.RxView
import constant.UiType
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.start_page.*
import listener.OnInitUiListener
import model.UiConfig
import model.UpdateConfig
import mvp.ljb.kt.act.BaseMvpActivity
import update.UpdateAppUtils
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class StartPageActivity : BaseMvpActivity<StartPageContract.IPresenter>(), StartPageContract.IView {


    private var slogin: String? = null
    private lateinit var mDisposable: Disposable
    private lateinit var context: Context
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var sp: SharedPreferences


    override fun registerPresenter() = StartPagePresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.start_page
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        val window = window
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
        )
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        context = this
        MainActivity.bool = false
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        mDisposable = Flowable.intervalRange(0, 4, 0, 1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { t ->
                time.text = (3 - t).toString()
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

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
