package com.example.xiaobai.music.music.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.config.Screenshot
import com.example.xiaobai.music.music.contract.ShareContract
import com.example.xiaobai.music.music.presenter.SharePresenter
import com.example.xiaobai.music.music.view.act.CodeListActivity
import com.example.xiaobai.music.utils.QRCodeCreator
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_code.*
import mvp.ljb.kt.fragment.BaseMvpFragment
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/14
 * @Description input description
 **/
class ShareFragment : BaseMvpFragment<ShareContract.IPresenter>(), ShareContract.IView {

    override fun registerPresenter() = SharePresenter::class.java

    companion object {
        lateinit var observer: Observer<Map<String,String>>
    }
    override fun getLayoutId(): Int {
       return R.layout.fragment_code
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
    }

    override fun initData() {
        super.initData()
        context?.let { getPresenter().usercode(it) }
    }

    override fun onResume() {
        super.onResume()

        context?.let { getPresenter().usercode(it) }

        observer = object : Observer<Map<String,String>> {
            override fun onSubscribe(d: Disposable) {}
            @SuppressLint("SetTextI18n")
            override fun onNext(codemap: Map<String,String>) {
                val num = codemap["invite_num"].toString().toInt()
                if(num==0){
                    MusicApp.setMinute(10)
                }else{
                    MusicApp.setMinute(codemap["invite_num"].toString().toInt()*10)
                }

                code.text = getText(R.string.set4).toString()+":"+codemap["invite_code"]+","+getText(R.string.set4_suss).toString()+codemap["invite_num"]+getText(R.string.set4_set).toString()
                codeima.setImageBitmap(QRCodeCreator.createQRCode(codemap["apk"].toString(),400,400,null))
            }
            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        RxView.clicks(btn2)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                Screenshot.screenShot(requireActivity())
            }

        RxView.clicks(codelist)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, CodeListActivity().javaClass) }
                startActivity(intent)
            }

    }
}
