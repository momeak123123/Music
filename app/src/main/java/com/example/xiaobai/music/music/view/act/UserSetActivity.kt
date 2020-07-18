package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.annotation.Nullable
import com.example.xiaobai.music.R
import com.example.xiaobai.music.music.contract.UserSetContract
import com.example.xiaobai.music.music.presenter.UserSetPresenter
import com.example.xiaobai.music.utils.FilesUtils
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog.SingleButtonCallback
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.head.*
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
class UserSetActivity : BaseMvpActivity<UserSetContract.IPresenter>(), UserSetContract.IView {

    companion object {
        lateinit var observer: Observer<String>
    }

    override fun registerPresenter() = UserSetPresenter::class.java
    private lateinit var file: File
    private lateinit var context: Context
    private lateinit var sp: SharedPreferences
    override fun getLayoutId(): Int {
        return R.layout.user_set
    }

    @SuppressLint("SetTextI18n")
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
        top_title.text = getText(R.string.set)
        sp = getSharedPreferences("User", Context.MODE_PRIVATE)
        val path = context.externalCacheDir!!.absolutePath+"/video-cache"
        file = File(path)
        val sizes = FilesUtils.getCurrentFolderSize(file)
        size.text = FormetFileSize(sizes)


    }

    private fun FormetFileSize(file: Long): String {
        val df = DecimalFormat("#.00")
        val fileSizeString: String
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
                val intent = Intent()
                context.let { intent.setClass(it, ChangePassActivity().javaClass) }
                startActivity(intent)
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
                if (code.text == "") {
                    MaterialDialog.Builder(context)
                        .title(getText(R.string.set4))
                        .inputType(
                            InputType.TYPE_CLASS_TEXT
                                    or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                        )
                        .input(
                            getString(R.string.code_captcha),
                            "",
                            false
                        ) { _, _ -> }
                        .inputRange(6, 6)
                        .positiveText(getText(R.string.carry))
                        .negativeText(getText(R.string.cancel))
                        .positiveColorRes(R.color.colorAccentDarkTheme)
                        .negativeColorRes(R.color.red)
                        .onPositive(SingleButtonCallback { dialog: MaterialDialog, _: DialogAction? ->
                            getPresenter().code(context,dialog.inputEditText!!.text.toString())

                        })
                        .cancelable(false)
                        .show()
                } else {
                    Toast.makeText(
                        context,
                        getText(R.string.code_captchas),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }

    }


    override fun initData() {
        super.initData()

        try {
            val co = getClipboardContent(context)
            val ca = co.substring(1)
            val da = ca.substring(0, ca.lastIndexOf(']'))

            if (code.text == "") {
                if (da != "") {
                    MaterialDialog.Builder(context)
                        .title(getText(R.string.set4))
                        .content(getText(R.string.code_captch))
                        .positiveColorRes(R.color.colorAccentDarkTheme)
                        .negativeColorRes(R.color.red)
                        .positiveText(getText(R.string.carry))
                        .negativeText(getText(R.string.cancel))
                        .onPositive { _: MaterialDialog?, _: DialogAction? ->
                            getPresenter().code(context,da)
                        }
                        .show()
                }
            }
        }catch (e:java.lang.Exception){}

    }

    /**
     * 获取剪切板上的内容
     */
    @Nullable
    fun getClipboardContent(context: Context): String {
        val cm: ClipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val data: ClipData? = cm.primaryClip
        if (data != null) {
            if (data.itemCount > 0) {
                val item: ClipData.Item = data.getItemAt(0)
                val sequence: CharSequence = item.coerceToText(context)
                return sequence.toString()
            }
        }
        return ""
    }

    override fun onResume() {
        super.onResume()
        observer = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(text: String) {
                      code.text = text
            }
            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }
    }


    override fun onDestroy() {
        super.onDestroy()

    }
}
