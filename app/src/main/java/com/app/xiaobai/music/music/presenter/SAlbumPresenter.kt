package com.app.xiaobai.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.app.xiaobai.music.music.contract.SAlbumContract
import com.app.xiaobai.music.music.model.SAlbumModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/24
 * @Description input description
 **/
class SAlbumPresenter : BaseMvpPresenter<SAlbumContract.IView, SAlbumContract.IModel>(), SAlbumContract.IPresenter{

    override fun registerModel() = SAlbumModel::class.java

}
