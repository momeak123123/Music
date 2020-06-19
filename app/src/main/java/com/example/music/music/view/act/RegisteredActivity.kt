package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.music.MainActivity
import com.example.music.MusicApp
import com.example.music.R
import com.example.music.music.contract.RegisteredContract
import com.example.music.music.presenter.RegisteredPresenter
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.view.enabled
import io.reactivex.Flowable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.internal.operators.flowable.FlowableBlockingSubscribe.subscribe
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registered.*
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.start_page.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class RegisteredActivity : BaseMvpActivity<RegisteredContract.IPresenter>(),
    RegisteredContract.IView {
    companion object {
        lateinit var observer: Observer<Boolean>
        lateinit var observers: Observer<Boolean>
    }
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

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        RxView.clicks(register_flot)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }

       /* RxView.clicks(btn_captcha)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                if (re_username_number.text.toString() != "") {
                    getPresenter().registercode(context,re_username_number.text.toString())
                }else{
                    Toast.makeText(context, R.string.error_name, Toast.LENGTH_SHORT).show()
                }
            }*/

        RxView.clicks(btn_register)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                if(MusicApp.getNetwork()){
                    if (re_username_number.text.toString() != "") {
                        if(isEmail(re_username_number.text.toString())){
                            if (re_pass_number.text.toString() != "") {
                                if (re_passs_number.text.toString() == re_pass_number.text.toString()) {

                                    getPresenter().registerdata(context,re_username_number.text.toString(),re_pass_number.text.toString())

                                } else {
                                    Toast.makeText(context, R.string.error_passs, Toast.LENGTH_SHORT).show()
                                }

                            } else {
                                Toast.makeText(context, R.string.error_pass, Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(context, R.string.tip_name_number_error, Toast.LENGTH_SHORT).show()
                        }


                    } else {
                        Toast.makeText(context, R.string.error_name, Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(
                        context,
                        getText(R.string.nonet),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
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
                    Toast.makeText(context, R.string.error_register, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

       /* observers = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            @SuppressLint("SetTextI18n")
            override fun onNext(bool: Boolean) {

                btn_captcha.isEnabled = false
                Flowable.intervalRange(0, 60 + 1, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    btn_captcha.text=""+(60-it)+"s"
                }
                .doOnComplete{
                    btn_captcha.isEnabled = true
                    btn_captcha.text= getText(R.string.captcha)
                }
                .subscribe()
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }*/


    }

}
