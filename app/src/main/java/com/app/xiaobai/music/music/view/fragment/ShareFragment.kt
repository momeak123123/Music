package com.app.xiaobai.music.music.view.fragment

import android.annotation.SuppressLint
import android.content.*
import android.view.View
import android.widget.Toast
import com.app.xiaobai.music.R
import com.app.xiaobai.music.config.Screenshot
import com.app.xiaobai.music.music.contract.ShareContract
import com.app.xiaobai.music.music.presenter.SharePresenter
import com.app.xiaobai.music.music.view.act.CodeListActivity
import com.app.xiaobai.music.music.view.act.LoginActivity
import com.app.xiaobai.music.utils.QRCodeCreator
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_code.*
import kotlinx.android.synthetic.main.fragment_code.code
import kotlinx.android.synthetic.main.fragment_uncode.*
import mvp.ljb.kt.fragment.BaseMvpFragment
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/14
 * @Description input description
 **/
class ShareFragment : BaseMvpFragment<ShareContract.IPresenter>(), ShareContract.IView {

    private lateinit var sp: SharedPreferences
    private var logins: Boolean = false

    override fun registerPresenter() = SharePresenter::class.java
    private lateinit var urls:String
    companion object {
        lateinit var observer: Observer<Map<String,String>>
    }
    override fun getLayoutId(): Int {
       return R.layout.fragment_code
    }


    override fun initData() {
        super.initData()
        sp = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE)
        logins = sp.getBoolean("login", false)
        if (logins) {
            context?.let { getPresenter().usercode(it) }
            include.visibility = View.VISIBLE
        }else{
            include.visibility = View.GONE

        }
    }

    override fun onResume() {
        super.onResume()

        logins = sp.getBoolean("login", false)
        if (logins) {
            context?.let { getPresenter().usercode(it) }
            include.visibility = View.GONE
        }else{
            include.visibility = View.VISIBLE

        }

        observer = object : Observer<Map<String,String>> {
            override fun onSubscribe(d: Disposable) {}
            @SuppressLint("SetTextI18n")
            override fun onNext(codemap: Map<String,String>) {
                var num = codemap["invite_num"].toString().toInt()
                if(num>0){
                    num /= 10
                }
                code.text = getText(R.string.set4s).toString()+","+getText(R.string.set4_suss).toString()+num+getText(R.string.set4_set).toString()
                urls = codemap["url"].toString()
                codeima.setImageBitmap(QRCodeCreator.createQRCode(urls,400,400,null))
            }
            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        RxView.clicks(btn2)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                Screenshot.screenShot(requireActivity())
            }

        RxView.clicks(btn1)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                clipboard()
            }

        RxView.clicks(codelist)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                if(logins){
                    val intent = Intent()
                    context?.let { intent.setClass(it, CodeListActivity().javaClass) }
                    startActivity(intent)
                }else{
                    Toast.makeText(
                        context,
                        getText(R.string.ungo),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }


        RxView.clicks(btn_login)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, LoginActivity().javaClass) }
                startActivity(intent)
            }

    }

    fun clipboard(){

        val str:ClipData=ClipData.newPlainText("Label",urls)
        val cm:ClipboardManager= requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(str)
        Toast.makeText(
            context,
            getText(R.string.code_copysuss),
            Toast.LENGTH_SHORT
        ).show()
    }

}

