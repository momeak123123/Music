package com.app.xiaobai.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.app.xiaobai.music.music.contract.WebViewContract
import com.app.xiaobai.music.music.model.WebViewModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/18
 * @Description input description
 **/
class WebViewPresenter : BaseMvpPresenter<WebViewContract.IView, WebViewContract.IModel>(), WebViewContract.IPresenter{

    override fun registerModel() = WebViewModel::class.java

}
