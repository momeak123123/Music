package com.app.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.app.xiaobai.music.IndexActivity
import com.app.xiaobai.music.MusicApp
import com.app.xiaobai.music.R
import com.app.xiaobai.music.music.contract.StartPageContract
import com.app.xiaobai.music.music.model.MusicPlayModel
import com.app.xiaobai.music.music.presenter.StartPagePresenter
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Flowable
import io.reactivex.Observable
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

    private lateinit var mDisposable: Disposable
    private lateinit var context: Context

    private  var num: Long = 3
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
        IndexActivity.bool = false
        Glide.with(this).load(MusicApp.getAds().img).placeholder(R.drawable.play_page_default_bg).into(adss)


        Observable.timer(5, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(disposable: Disposable) {}
                override fun onNext(number: Long) {
                    MusicPlayModel.updateapp(getVersionName())
                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })
    }

    private fun getVersionName(): String {
        // 包管理器 可以获取清单文件信息
        val packageManager = packageManager
        try {
            // 获取包信息
            // 参1 包名 参2 获取额外信息的flag 不需要的话 写0
            val packageInfo = packageManager.getPackageInfo(
                packageName, 0
            )
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        RxView.clicks(adss)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                val uri: Uri = Uri.parse(MusicApp.getAds().url)
                intent.data = uri
                startActivity(intent)
            }

        RxView.clicks(view)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {

            }


    }

    override fun initData() {
        super.initData()
        getPresenter().ads()

    }

    override fun onResume() {
        super.onResume()

        num =  MusicApp.getAds().seconds
        mDisposable = Flowable.intervalRange(0, num, 0, 1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { t ->
                time.text = (num-t).toString()
            }
            .doOnComplete {
                val intent = Intent(context, IndexActivity::class.java)
                startActivity(intent)
            }
            .subscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
