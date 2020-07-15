package com.example.xiaobai.music.music.presenter

import com.example.xiaobai.music.music.contract.ShareContract
import com.example.xiaobai.music.music.model.ShareModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/14
 * @Description input description
 **/
class SharePresenter : BaseMvpPresenter<ShareContract.IView, ShareContract.IModel>(), ShareContract.IPresenter{

    override fun registerModel() = ShareModel::class.java

}
