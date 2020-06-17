package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.music.contract.UserEditContract
import com.example.music.music.presenter.UserEditPresenter
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.user_edit.*
import kotlinx.android.synthetic.main.user_edit.city
import kotlinx.android.synthetic.main.user_edit.name
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class UserEditActivity : BaseMvpActivity<UserEditContract.IPresenter>(), UserEditContract.IView {


    private lateinit var context: Context

    override fun registerPresenter() = UserEditPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.user_edit
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context =this
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
                if (name.text.toString() != "") {
                    if (gender.text.toString() != "") {
                        if (city.text.toString() != "") {
                            getPresenter().registerdata(
                                context,
                                name.text.toString(),
                                gender.text.toString(),
                                city.text.toString()
                            )
                        } else {
                            Toast.makeText(context, R.string.error_user, Toast.LENGTH_SHORT)
                                .show()
                        }

                    } else {
                        Toast.makeText(context, R.string.error_user, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, R.string.error_user, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun initData() {
        super.initData()

        val sp =
            getSharedPreferences("User", Context.MODE_PRIVATE)
        Glide.with(context).load(sp.getString("url", "")).placeholder(R.color.main_black_grey).into(ima)
        name.text = Editable.Factory.getInstance().newEditable(sp.getString("name", ""))
        gender.text = Editable.Factory.getInstance().newEditable(sp.getString("gender", ""))
        city.text = Editable.Factory.getInstance().newEditable(sp.getString("city", ""))
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
