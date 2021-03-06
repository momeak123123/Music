package com.app.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.app.xiaobai.music.MusicApp
import com.app.xiaobai.music.R
import com.app.xiaobai.music.music.contract.LoginContract
import com.app.xiaobai.music.music.presenter.LoginPresenter
import com.app.xiaobai.music.sql.dao.mPlaylistDao
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
    private lateinit var sp: SharedPreferences
    override fun registerPresenter() = LoginPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
        sp = getSharedPreferences("User", Context.MODE_PRIVATE)

        val left = resources.getDrawable(R.drawable.emil,null)
        left.setBounds(0, 0, 50, 50) //必须设置图片的大小否则没有作用
        et_username_number.setCompoundDrawables(left, null, null, null)

        val lefts = resources.getDrawable(R.drawable.pass,null)
        lefts.setBounds(0, 0, 50, 50) //必须设置图片的大小否则没有作用
        et_passs_number.setCompoundDrawables(lefts, null, null, null)
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
                val view: View? = this.currentFocus
                if (view != null) {
                    val inputMethodManager =
                        (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                    inputMethodManager.hideSoftInputFromWindow(
                        view.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
                if(MusicApp.network()!=-1){
                    if (et_username_number.text.toString() != "") {
                        if(isEmail(et_username_number.text.toString())){
                            if (et_passs_number.text.toString() != "") {

                                getPresenter().logindata(
                                    context,
                                    et_username_number.text.toString(),
                                    et_passs_number.text.toString()
                                )
                                if(sp.getString("username","")!=""){
                                    if(et_username_number.text.toString() != sp.getString("username","")){
                                        val list = mPlaylistDao.queryAll()
                                        if (list.size > 0) {
                                                for(its in list){
                                                    mPlaylistDao.delete(its.id)
                                                }
                                        }
                                    }
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

    override fun initData() {
        super.initData()
    }

    fun isEmail(email: String): Boolean {
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
