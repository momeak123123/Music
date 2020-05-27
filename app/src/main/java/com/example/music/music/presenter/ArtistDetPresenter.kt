package com.example.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.ArtistDetContract
import com.example.music.music.model.ArtistDetModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class ArtistDetPresenter : BaseMvpPresenter<ArtistDetContract.IView, ArtistDetContract.IModel>(), ArtistDetContract.IPresenter{

    override fun registerModel() = ArtistDetModel::class.java

}
