package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.xiaobai.music.R
import com.example.xiaobai.music.music.contract.SetContract
import com.example.xiaobai.music.music.presenter.SetPresenter
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.my_set.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/01
 * @Description input description
 **/
class SetActivity : BaseMvpActivity<SetContract.IPresenter>() , SetContract.IView {

    private lateinit var context: Context
    private lateinit var sp: SharedPreferences
    override fun registerPresenter() = SetPresenter::class.java

    override fun getLayoutId(): Int {
       return R.layout.my_set
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
        top_title.text = getText(R.string.set)
        sp = getSharedPreferences("User", Context.MODE_PRIVATE)
    }


    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        RxView.clicks(top_flot)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }

        RxView.clicks(btn_unlogin)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                sp.edit().putBoolean("login", false).apply()
                val intent = Intent()
                intent.setClass(context, LoginActivity().javaClass)
                startActivity(intent)
            }


    }

}
