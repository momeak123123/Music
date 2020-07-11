package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.music.contract.RegisteredContract
import com.example.xiaobai.music.music.presenter.RegisteredPresenter
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registered.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit
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

        val left = resources.getDrawable(R.drawable.emil,null)
        left.setBounds(0, 0, 50, 50) //必须设置图片的大小否则没有作用
        re_username_number.setCompoundDrawables(left, null, null, null)

        val lefts = resources.getDrawable(R.drawable.pass,null)
        lefts.setBounds(0, 0, 50, 50) //必须设置图片的大小否则没有作用
        re_pass_number.setCompoundDrawables(lefts, null, null, null)

        val leftt = resources.getDrawable(R.drawable.pass,null)
        leftt.setBounds(0, 0, 50, 50) //必须设置图片的大小否则没有作用
        re_passs_number.setCompoundDrawables(leftt, null, null, null)
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
                if(MusicApp.network()!=-1){
                    if (re_username_number.text.toString() != "") {
                        if(isEmail(re_username_number.text.toString())){
                            if (re_pass_number.text.toString() != "") {
                                if (re_passs_number.text.toString() == re_pass_number.text.toString()) {

                                    if(radioButton.isChecked){
                                        getPresenter().registerdata(context,re_username_number.text.toString(),re_pass_number.text.toString())
                                    } else {
                                        Toast.makeText(context, R.string.error_captchas, Toast.LENGTH_SHORT).show()
                                    }

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
                        getText(R.string.error_connection),
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
