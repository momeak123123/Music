package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.annotation.Nullable
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.music.contract.ChangePassContract
import com.example.xiaobai.music.music.presenter.ChangePassPresenter
import com.example.xiaobai.music.sql.dao.mPlaylistDao
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_changepass.*
import kotlinx.android.synthetic.main.head.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/17
 * @Description input description
 **/
class ChangePassActivity : BaseMvpActivity<ChangePassContract.IPresenter>(),
    ChangePassContract.IView {

    override fun registerPresenter() = ChangePassPresenter::class.java
    private lateinit var context: Context
    private lateinit var sp: SharedPreferences
    override fun getLayoutId(): Int {
        return R.layout.activity_changepass
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
        sp = getSharedPreferences("User", Context.MODE_PRIVATE)

        top_title.text=getText(R.string.set1)
        val lefts = resources.getDrawable(R.drawable.pass, null)
        lefts.setBounds(0, 0, 50, 50) //必须设置图片的大小否则没有作用
        re_pass_number.setCompoundDrawables(lefts, null, null, null)

        val leftt = resources.getDrawable(R.drawable.pass, null)
        leftt.setBounds(0, 0, 50, 50) //必须设置图片的大小否则没有作用
        re_passs_number.setCompoundDrawables(leftt, null, null, null)

    }


    override fun initData() {
        super.initData()

    }


    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        RxView.clicks(top_flot)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }

        RxView.clicks(btn_register)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                if (MusicApp.network() != -1) {
                    if (re_pass_number.text.toString() != "") {
                        if (re_passs_number.text.toString() != "") {
                            getPresenter().data(
                                context,
                                re_pass_number.text.toString(),
                                re_passs_number.text.toString()
                            )

                        } else {
                            Toast.makeText(context, R.string.error_pass, Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(context, R.string.error_pass, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        getText(R.string.error_connection),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
    }

    override fun onResume() {
        super.onResume()

    }

}
