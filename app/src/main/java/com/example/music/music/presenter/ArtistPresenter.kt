package com.example.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.ArtistContract
import com.example.music.music.model.ArtistModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class ArtistPresenter : BaseMvpPresenter<ArtistContract.IView, ArtistContract.IModel>(), ArtistContract.IPresenter{

    override fun registerModel() = ArtistModel::class.java

}
