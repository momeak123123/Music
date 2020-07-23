package com.app.xiaobai.music.music.presenter

import com.app.xiaobai.music.music.contract.DownloadContract
import com.app.xiaobai.music.music.model.DownloadModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/20
 * @Description input description
 **/
class DownloadPresenter : BaseMvpPresenter<DownloadContract.IView, DownloadContract.IModel>(), DownloadContract.IPresenter{

    override fun registerModel() = DownloadModel::class.java

}
