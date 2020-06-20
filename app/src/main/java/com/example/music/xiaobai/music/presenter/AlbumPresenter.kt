package com.example.music.xiaobai.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.xiaobai.music.contract.AlbumContract
import com.example.music.xiaobai.music.model.AlbumModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class AlbumPresenter : BaseMvpPresenter<AlbumContract.IView, AlbumContract.IModel>(), AlbumContract.IPresenter{

    override fun registerModel() = AlbumModel::class.java

}
