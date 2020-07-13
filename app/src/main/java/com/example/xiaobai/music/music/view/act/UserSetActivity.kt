package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.xiaobai.music.R
import com.example.xiaobai.music.music.contract.UserSetContract
import com.example.xiaobai.music.music.presenter.UserSetPresenter
import com.example.xiaobai.music.utils.FilesUtils
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.sleep.*
import kotlinx.android.synthetic.main.user_set.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/07
 * @Description input description
 **/
class UserSetActivity : BaseMvpActivity<UserSetContract.IPresenter>() , UserSetContract.IView {

    override fun registerPresenter() = UserSetPresenter::class.java
    private lateinit var file: File
    private lateinit var context: Context
    private lateinit var sp: SharedPreferences
    override fun getLayoutId(): Int {
       return R.layout.user_set
    }

    private lateinit var mTimeOption: Array<String?>

    @SuppressLint("SetTextI18n")
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
        top_title.text = getText(R.string.set)
        sp = getSharedPreferences("User", Context.MODE_PRIVATE)
        val path = context.externalCacheDir!!.absolutePath+"/video-cache"
        file = File(path)
        val sizes = FilesUtils.getCurrentFolderSize(file)
        size.text =FormetFileSize(sizes)


    }

     private fun FormetFileSize(file: Long): String {
         val df = DecimalFormat("#.00")
         var fileSizeString = "0M"
         fileSizeString = when {
             file < 1024 -> {
                 df.format(file.toDouble()).toString() + "B"
             }
             file < 1048576 -> {
                 df.format(file.toDouble() / 1024).toString() + "K"
             }
             file < 1073741824 -> {
                 df.format(file.toDouble() / 1048576).toString() + "M"
             }
             else -> {
                 df.format(file.toDouble() / 1073741824).toString() + "G"
             }
         }
         return fileSizeString
     }

    @SuppressLint("CheckResult", "SetTextI18n")
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
                    .title(getText(R.string.set2))
                    .content(getText(R.string.set2_un))
                    .positiveColorRes(R.color.colorAccentDarkTheme)
                    .negativeColorRes(R.color.red)
                    .positiveText(getText(R.string.carry))
                    .negativeText(getText(R.string.cancel))
                    .onPositive { _: MaterialDialog?, _: DialogAction? ->

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

        RxView.clicks(view3)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
              set.visibility = View.VISIBLE
            }

        RxView.clicks(popule_back)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                set.visibility = View.GONE
            }

        RxView.clicks(relativeLayout2)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                set.visibility = View.GONE
            }

        RxView.clicks(relat1)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if(switch1.isChecked){
                    switch1.isChecked = false
                }else{
                    switch1.isChecked = true
                    switch2.isChecked = false
                }


            }
        RxView.clicks(relat2)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                TimePickerDialog(this,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute -> time.text =
                        "$hourOfDay:$minute"
                    }, 23, 0, true).show()

            }
        RxView.clicks(relat3)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if(switch2.isChecked){
                    switch2.isChecked = false
                }else{
                    switch2.isChecked = true
                    switch1.isChecked = false
                }

            }
        RxView.clicks(relat4)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                TimePickerDialog(this,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        time.text = "$hourOfDay:$minute"
                        sp.edit().putString("starttime", "$hourOfDay:$minute").apply()
                    }, 23, 0, true).show()

            }

        RxView.clicks(relat5)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                TimePickerDialog(this,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        times.text = "$hourOfDay:$minute"
                        sp.edit().putString("endtime", "$hourOfDay:$minute").apply()
                    }, 2, 0, true).show()

            }
    }

    override fun initData() {
        super.initData()


    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
