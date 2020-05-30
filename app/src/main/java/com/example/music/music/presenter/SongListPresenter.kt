package com.example.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.SongListContract
import com.example.music.music.model.SongListModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class SongListPresenter : BaseMvpPresenter<SongListContract.IView, SongListContract.IModel>(), SongListContract.IPresenter{

    override fun registerModel() = SongListModel::class.java

}
