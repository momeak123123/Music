package com.example.xiaobai.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.xiaobai.music.music.contract.DownloadContract
import com.example.xiaobai.music.music.model.DownloadModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/20
 * @Description input description
 **/
class DownloadPresenter : BaseMvpPresenter<DownloadContract.IView, DownloadContract.IModel>(), DownloadContract.IPresenter{

    override fun registerModel() = DownloadModel::class.java

}
