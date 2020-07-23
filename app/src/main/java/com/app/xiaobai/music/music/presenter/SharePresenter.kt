package com.app.xiaobai.music.music.presenter

import android.content.Context
import com.app.xiaobai.music.music.contract.ShareContract
import com.app.xiaobai.music.music.model.ShareModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/14
 * @Description input description
 **/
class SharePresenter : BaseMvpPresenter<ShareContract.IView, ShareContract.IModel>(), ShareContract.IPresenter{

    override fun registerModel() = ShareModel::class.java
    override fun usercode(context: Context) {
        getModel().usercode(context)
    }

}
