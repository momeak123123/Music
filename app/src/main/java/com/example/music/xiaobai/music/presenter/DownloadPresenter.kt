package com.example.music.xiaobai.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.xiaobai.music.contract.DownloadContract
import com.example.music.xiaobai.music.model.DownloadModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/20
 * @Description input description
 **/
class DownloadPresenter : BaseMvpPresenter<DownloadContract.IView, DownloadContract.IModel>(), DownloadContract.IPresenter{

    override fun registerModel() = DownloadModel::class.java

}
