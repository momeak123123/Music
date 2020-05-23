package com.example.music.music.view.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.music.MainActivity
import com.example.music.R
import com.example.music.music.contract.RegisteredContract
import com.example.music.music.presenter.RegisteredPresenter
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registered.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class RegisteredActivity : BaseMvpActivity<RegisteredContract.IPresenter>(),
    RegisteredContract.IView {

    private lateinit var context: Context
    override fun registerPresenter() = RegisteredPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.activity_registered
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
    }

    override fun initData() {
        super.initData()
        register_flot.setOnClickListener {
            finish()
        }
    }

    override fun initView() {
        super.initView()
        btn_register.setOnClickListener {
            if (re_username_number.isNotEmpty) {
                if (re_pass_number.isNotEmpty) {
                    if (re_passs_number.text.toString().equals(re_pass_number.text.toString())) {
                        if (getPresenter().registerdata()) {
                            finish()
                        } else {
                            Toast.makeText(context, R.string.error_register, Toast.LENGTH_SHORT)
                                .show()
                        }

                    } else {
                        Toast.makeText(context, R.string.error_passs, Toast.LENGTH_SHORT).show()
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
