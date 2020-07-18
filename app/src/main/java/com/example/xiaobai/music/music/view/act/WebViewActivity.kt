package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import com.example.xiaobai.music.R
import com.example.xiaobai.music.music.contract.WebViewContract
import com.example.xiaobai.music.music.presenter.WebViewPresenter
import kotlinx.android.synthetic.main.activity_webview.*
import mvp.ljb.kt.act.BaseMvpActivity


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/18
 * @Description input description
 **/
class WebViewActivity : BaseMvpActivity<WebViewContract.IPresenter>() , WebViewContract.IView {

    override fun registerPresenter() = WebViewPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.activity_webview
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        val bundle = intent.extras
        val url = bundle?.get("url") as String
        web_view.settings.javaScriptEnabled = true
        web_view.webViewClient = WebViewClient()
        web_view.loadUrl(url)
    }
}
