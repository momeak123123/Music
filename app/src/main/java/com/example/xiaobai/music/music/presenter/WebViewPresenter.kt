package com.example.xiaobai.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.xiaobai.music.music.contract.WebViewContract
import com.example.xiaobai.music.music.model.WebViewModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/18
 * @Description input description
 **/
class WebViewPresenter : BaseMvpPresenter<WebViewContract.IView, WebViewContract.IModel>(), WebViewContract.IPresenter{

    override fun registerModel() = WebViewModel::class.java

}
