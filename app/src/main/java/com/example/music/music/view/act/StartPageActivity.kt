package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import com.example.music.MainActivity
import com.example.music.R
import com.example.music.bean.Music
import com.example.music.config.IntentRecevier
import com.example.music.music.contract.StartPageContract
import com.example.music.music.presenter.StartPagePresenter
import com.example.music.music.view.fragment.HomeFragment
import com.google.gson.JsonObject
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.start_page.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class StartPageActivity : BaseMvpActivity<StartPageContract.IPresenter>() , StartPageContract.IView {

    private lateinit var intentRecevier: IntentRecevier
    private lateinit var context: Context
    private lateinit var countDownTimer: CountDownTimer

    override fun registerPresenter() = StartPagePresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.start_page
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context=this

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        intentRecevier = IntentRecevier()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        this.registerReceiver(intentRecevier,filter)
        countDownTimer = object : CountDownTimer(3000 + 500, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                time.text=(millisUntilFinished / 1000).toString()
            }
            override fun onFinish() {
                val intent = Intent()
                intent.setClass(context as StartPageActivity, MainActivity().javaClass)
                startActivity(intent)
            }
        }.start()


        val token = "BdQM4VLl4Z2xFqFl"

        val sp: SharedPreferences =context.getSharedPreferences("Music", Context.MODE_PRIVATE)

        sp.edit().putString("token", token).apply()


        RxView.clicks(view)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                countDownTimer.cancel()
                countDownTimer.onFinish()
            }
    }

    override fun initData() {
        super.initData()
       getPresenter().homedata(context)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(intentRecevier)
    }
}
