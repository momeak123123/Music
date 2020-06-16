package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.music.MainActivity
import com.example.music.R
import com.example.music.music.contract.LoginContract
import com.example.music.music.presenter.LoginPresenter
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_login.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class LoginActivity : BaseMvpActivity<LoginContract.IPresenter>(), LoginContract.IView {

    companion object {
        lateinit var observer: Observer<Boolean>
    }

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
        RxView.clicks(register_text)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                intent.setClass(context as LoginActivity, RegisteredActivity().javaClass)
                startActivity(intent)
                finish()
            }
        RxView.clicks(login_flot)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }
        RxView.clicks(btn_login)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                if (et_username_number.text.toString() != "") {
                    if(isEmail(et_username_number.text.toString())){
                        if (et_passs_number.text.toString() != "") {

                            getPresenter().logindata(
                                context,
                                et_username_number.text.toString(),
                                et_passs_number.text.toString()
                            )
                        } else {
                            Toast.makeText(context, R.string.error_pass, Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context, R.string.tip_name_number_error, Toast.LENGTH_SHORT).show()
                    }


                } else {
                    Toast.makeText(context, R.string.error_name, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun initData() {
        super.initData()
    }

    fun isEmail(email: String?): Boolean {
        val str =
            "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"
        val p = Pattern.compile(str)
        val m = p.matcher(email)
        return m.matches()
    }

    override fun onResume() {
        super.onResume()

        observer = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(bool: Boolean) {
                if (bool) {
                    finish()
                } else {

                    Toast.makeText(context, R.string.error_login, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }


    }
}
