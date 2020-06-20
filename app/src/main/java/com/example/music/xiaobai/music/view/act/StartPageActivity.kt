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
import com.example.music.xiaobai.MainActivity
import com.example.music.xiaobai.R
import com.example.music.xiaobai.music.contract.StartPageContract
import com.example.music.xiaobai.music.presenter.StartPagePresenter
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.start_page.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class StartPageActivity : BaseMvpActivity<StartPageContract.IPresenter>() , StartPageContract.IView {


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
        context=this
        MainActivity.bool = false
        sp = getSharedPreferences("User", Context.MODE_PRIVATE)
        slogin = sp.getString("user_id", "")
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
                if (slogin == "") {
                    val intent = Intent()
                    intent.setClass(context, LoginActivity().javaClass)
                    startActivity(intent)
                }
            }
            .subscribe()

        RxView.clicks(view)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                mDisposable.dispose()
                finish()
                if (slogin == "") {
                    val intent = Intent()
                    intent.setClass(context, LoginActivity().javaClass)
                    startActivity(intent)
                }
            }
    }

    override fun initData() {
        super.initData()
       // context.let { getPresenter().homedata(it) }
        //Toast.makeText(context, NetWorkUtils.getNetworkTypeName(context), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
