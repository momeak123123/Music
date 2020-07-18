package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import com.example.xiaobai.music.R
import com.example.xiaobai.music.music.contract.WebViewContract
import com.example.xiaobai.music.music.presenter.WebViewPresenter
import im.delight.android.webview.AdvancedWebView
import kotlinx.android.synthetic.main.activity_webview.*
import mvp.ljb.kt.act.BaseMvpActivity


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/18
 * @Description input description
 **/
class WebViewActivity : BaseMvpActivity<WebViewContract.IPresenter>() , WebViewContract.IView , AdvancedWebView.Listener {

    override fun registerPresenter() = WebViewPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.activity_webview
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        val bundle = intent.extras
        val url = bundle?.get("url") as String
        mWebView.setListener(this, this)
        mWebView.loadUrl(url)


    }


    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        mWebView.onResume()
    }

    @SuppressLint("NewApi")
    override fun onPause() {
        mWebView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mWebView.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        mWebView.onActivityResult(requestCode, resultCode, intent)
    }

    override fun onBackPressed() {
        if (!mWebView.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPageFinished(url: String?) {
        println("Xiazai1$url")
    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
        println("Xiazai2$failingUrl")
    }

    override fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) {
        println("Xiazai3$url")
    }

    override fun onExternalPageRequest(url: String?) {
        println("Xiazai4$url")
    }

    override fun onPageStarted(url: String?, favicon: Bitmap?) {
        println("Xiazai5$url")
    }
}

