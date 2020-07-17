package com.example.xiaobai.music

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.xiaobai.music.config.Constants
import com.example.xiaobai.music.config.Installation
import com.example.xiaobai.music.music.model.MainModel
import com.example.xiaobai.music.music.model.MusicPlayModel.Companion.getadvertising
import com.example.xiaobai.music.music.model.MusicPlayModel.Companion.updateapp
import com.example.xiaobai.music.utils.DeleteUtil
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class StartsActivity : AppCompatActivity() {

    private var mdDisposable: Disposable? = null

    @SuppressLint("SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
        )
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT

        MainModel.homedata(this)
        getadvertising()
        mdDisposable = Flowable.intervalRange(0, 3, 0, 1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {}
            .doOnComplete {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
            .subscribe()

        Observable.timer(10, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(disposable: Disposable) {}
                override fun onNext(number: Long) {
                    updateapp(getVersionName())
                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })

        println("日期" + Constants.Dates())
        println("Installtion ID" + Installation.getUniqueID(this))
        //67700683-bb9b-4ef4-b30b-8c332a98aa2e
        val sp: SharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)

        if(sp.getString("android_id", "").toString()==""){
            DeleteUtil.delete(getExternalFilesDir("")!!.absolutePath+"/download", false, "")
            sp.edit().putString("android_id", Installation.getUniqueID(this)).apply()
        }

        if(sp.getString("down_date", "").toString()==""){
            sp.edit().putInt("down_num", 0).apply()
            sp.edit().putString("down_date", Constants.Dates()).apply()
        }

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

    override fun onResume() {
        super.onResume()
    }
}
