package com.example.xiaobai.music

import android.annotation.SuppressLint
import android.app.Activity
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

class StartsActivity : Activity() {

    @SuppressLint("SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Flowable.intervalRange(0, 1, 0, 1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {}
            .doOnComplete {
                startActivity(Intent(applicationContext, IndexActivity::class.java))
            }
            .subscribe()

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


        getadvertising()

        MainModel.homedata(this)

        Observable.timer(8, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(disposable: Disposable) {}
                override fun onNext(number: Long) {
                    updateapp(getVersionName())
                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })

        val sp: SharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)

        if(sp.getString("android_id", "").toString()==""){
            DeleteUtil.delete(getExternalFilesDir("")!!.absolutePath+"/download", false, "")
            sp.edit().putString("android_id", Installation.getUniqueID(this)).apply()
        }
    }
}
