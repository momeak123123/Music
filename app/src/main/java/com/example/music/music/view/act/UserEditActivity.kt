package com.example.music.music.view.act

import android.annotation.SuppressLint
import com.example.music.R
import com.example.music.music.contract.UserEditContract
import com.example.music.music.presenter.UserEditPresenter
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.user_edit.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class UserEditActivity : BaseMvpActivity<UserEditContract.IPresenter>() , UserEditContract.IView {

    override fun registerPresenter() = UserEditPresenter::class.java

    override fun getLayoutId(): Int {
       return R.layout.user_edit
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        RxView.clicks(top_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }

        RxView.clicks(btn_edit)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {

            }
    }

    override fun initData() {
        super.initData()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
