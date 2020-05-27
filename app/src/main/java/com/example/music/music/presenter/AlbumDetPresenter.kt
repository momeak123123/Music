package com.example.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.AlbumDetContract
import com.example.music.music.model.AlbumDetModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class AlbumDetPresenter : BaseMvpPresenter<AlbumDetContract.IView, AlbumDetContract.IModel>(), AlbumDetContract.IPresenter{

    override fun registerModel() = AlbumDetModel::class.java

}
