package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.example.xiaobai.music.MainActivity
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.SongDetAdapter
import com.example.xiaobai.music.bean.Banner
import com.example.xiaobai.music.music.contract.StartPageContract
import com.example.xiaobai.music.music.presenter.StartPagePresenter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Flowable
import io.reactivex.Observer
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
class StartPageActivity : BaseMvpActivity<StartPageContract.IPresenter>(), StartPageContract.IView {
    companion object {
        lateinit var observer: Observer<JsonArray>
    }
    private lateinit var mDisposable: Disposable
    private lateinit var context: Context


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
        getPresenter().ads()

    }

    override fun onResume() {
        super.onResume()
        observer = object : Observer<JsonArray> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: JsonArray) {
                val ads: List<Banner> = Gson().fromJson<Array<Banner>>(
                    data,
                    Array<Banner>::class.java
                ).toList()

               if(ads.isNotEmpty()){
                   //Glide.with(context).load(ads[0].url).placeholder(R.color.colorPrimaryDark).into(adss)
               }


            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
