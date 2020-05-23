package com.example.music.music.view.act

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import com.example.music.MainActivity
import com.example.music.R
import com.example.music.bean.Music
import com.example.music.config.IntentRecevier
import com.example.music.music.contract.StartPageContract
import com.example.music.music.presenter.StartPagePresenter
import kotlinx.android.synthetic.main.start_page.*
import mvp.ljb.kt.act.BaseMvpActivity

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class StartPageActivity : BaseMvpActivity<StartPageContract.IPresenter>() , StartPageContract.IView {

    companion object{

        var Datas = mutableListOf<Music>()
    }
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
                finish()
                val intent = Intent()
                intent.setClass(context as StartPageActivity, MainActivity().javaClass)
                startActivity(intent)
            }
        }.start()

        view.setOnClickListener {
            countDownTimer.cancel()
            countDownTimer.onFinish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(intentRecevier)
    }
}
