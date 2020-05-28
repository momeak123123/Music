package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.music.R
import com.example.music.music.contract.LoginContract
import com.example.music.music.presenter.LoginPresenter
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.head.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class LoginActivity : BaseMvpActivity<LoginContract.IPresenter>(), LoginContract.IView {

    private lateinit var context: Context

    override fun registerPresenter() = LoginPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        register_text.setOnClickListener {
            val intent = Intent()
            intent.setClass(context as LoginActivity, RegisteredActivity().javaClass)
            startActivity(intent)
        }
        RxView.clicks(login_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }
    }

    override fun initData() {
        super.initData()
        btn_login.setOnClickListener {
            if (et_username_number.isNotEmpty) {
                if (et_passs_number.isNotEmpty) {
                    if (getPresenter().logindata(context)) {
                        /* finish()
                         val intent = Intent()
                         intent.setClass(context as LoginActivity, MainActivity().javaClass)
                         startActivity(intent)*/
                    } else {
                        Toast.makeText(context, R.string.error_login, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, R.string.error_pass, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, R.string.error_name, Toast.LENGTH_SHORT).show()
            }
        }

    }
}
