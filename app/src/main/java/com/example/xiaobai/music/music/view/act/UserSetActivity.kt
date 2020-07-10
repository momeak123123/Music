package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.xiaobai.music.R
import com.example.xiaobai.music.music.contract.UserSetContract
import com.example.xiaobai.music.music.presenter.UserSetPresenter
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.xuexiang.xui.widget.picker.widget.OptionsPickerView
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder
import com.xuexiang.xui.widget.picker.widget.listener.OnOptionsSelectListener
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.user_set.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/07
 * @Description input description
 **/
class UserSetActivity : BaseMvpActivity<UserSetContract.IPresenter>() , UserSetContract.IView {

    override fun registerPresenter() = UserSetPresenter::class.java
    private lateinit var context: Context
    private lateinit var sp: SharedPreferences
    override fun getLayoutId(): Int {
       return R.layout.user_set
    }

    private lateinit var mTimeOption: Array<String?>

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

        RxView.clicks(view1)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                MaterialDialog.Builder(context)
                    .title("清理缓存")
                    .content("是否清理缓存")
                    .positiveColorRes(R.color.colorAccentDarkTheme)
                    .negativeColorRes(R.color.red)
                    .positiveText("确认")
                    .negativeText("取消")
                    .onPositive { _: MaterialDialog?, _: DialogAction? ->
                       val path = context.externalCacheDir!!.absolutePath+"/video-cache"
                        val file = File(path)
                        if (file.isDirectory) {
                            val files: Array<File> = file.listFiles()

                            for (i in files.indices) {
                                val f = files[i]
                                try {
                                    f.delete()
                                } catch (e: Exception) {
                                }
                            }
                            Toast.makeText(
                                context,
                                getText(R.string.set2_sess),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (file.exists()) {
                            try {
                                file.delete()
                            } catch (e: java.lang.Exception) {
                            }
                            Toast.makeText(
                                context,
                                getText(R.string.set2_sess),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    .show()
            }

        RxView.clicks(view)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {

            }

        RxView.clicks(view2)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context.let { intent.setClass(it, UserEditActivity().javaClass) }
                startActivity(intent)
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

    override fun initData() {
        super.initData()


    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
