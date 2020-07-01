package com.example.xiaobai.music

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.xiaobai.music.music.model.MusicPlayModel.Companion.asd
import com.example.xiaobai.music.music.model.MusicPlayModel.Companion.updateapp
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class StartsActivity : AppCompatActivity() {

    companion object {
        lateinit var observer: Observer<Boolean>
    }

    private var mdDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mdDisposable = Flowable.intervalRange(0, 2, 0, 1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                asd(this)
            }
            .doOnComplete {
                val it = Intent(applicationContext, MainActivity::class.java)
                startActivity(it)
            }
            .subscribe()

        Observable.timer(6, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(disposable: Disposable) {}
                override fun onNext(number: Long) {
                    updateapp(getVersionName())
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

    override fun onResume() {
        super.onResume()
        observer = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(bool: Boolean) {
                if (bool) {
                    mdDisposable!!.dispose()
                    val it = Intent(applicationContext, MainActivity::class.java)
                    startActivity(it)
                }
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}
        }
    }
}
