package com.example.music.music.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.example.music.R
import com.example.music.music.contract.MyContract
import com.example.music.music.presenter.MyPresenter
import com.example.music.music.view.act.ArtistActivity
import com.example.music.music.view.act.UserEditActivity
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_my.*
import mvp.ljb.kt.fragment.BaseMvpFragment
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class MyFragment : BaseMvpFragment<MyContract.IPresenter>(), MyContract.IView {

    companion object {

    }

    override fun registerPresenter() = MyPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.fragment_my
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        RxView.clicks(btn_up)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, UserEditActivity().javaClass) }
                startActivity(intent)
            }
    }

}
