package com.example.xiaobai.music.music.presenter

import com.example.xiaobai.music.music.contract.AlbumContract
import com.example.xiaobai.music.music.model.AlbumModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class AlbumPresenter : BaseMvpPresenter<AlbumContract.IView, AlbumContract.IModel>(), AlbumContract.IPresenter{

    override fun registerModel() = AlbumModel::class.java

}
